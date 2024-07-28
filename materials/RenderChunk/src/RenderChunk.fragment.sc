$input v_color0, v_fog, v_texcoord0, v_lightmapUV, v_viewPos

#include <bgfx_shader.sh>

uniform vec4 FogColor;
uniform vec4 FogAndDistanceControl; // x: fogStart, y: fogEnd, z: pixelationStart, w: pixelationEnd
uniform vec4 ViewPositionAndTime; // x, y, z: camera position, w: time

SAMPLER2D(s_MatTexture, 0);
SAMPLER2D(s_SeasonsTexture, 1);
SAMPLER2D(s_LightMapTexture, 2);

vec2 applyPixelation(vec2 uv, float pixelSize) {
    vec2 pixelatedUV = floor(uv / pixelSize) * pixelSize;
    return pixelatedUV;
}

void main() {
    vec4 diffuse;

#if defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY)
    diffuse.rgb = vec3(1.0, 1.0, 1.0);
#else
    diffuse = texture2D(s_MatTexture, v_texcoord0);

#if defined(ALPHA_TEST)
    if (diffuse.a < 0.5) {
        discard;
    }
#endif

#if defined(SEASONS) && (defined(OPAQUE) || defined(ALPHA_TEST))
    diffuse.rgb *=
        mix(vec3(1.0, 1.0, 1.0),
            texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0, v_color0.b);
    diffuse.rgb *= v_color0.aaa;
#else
    diffuse *= v_color0;
#endif
#endif

#ifndef TRANSPARENT
    diffuse.a = 1.0;
#endif

    diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;

    // Calculate distance from camera to fragment
    float distance = length(ViewPositionAndTime.xyz - v_viewPos.xyz);

    // Apply pixelation effect based on distance
    float pixelSize = 1.0;
    if (distance > FogAndDistanceControl.z) {
        float t = clamp((distance - FogAndDistanceControl.z) / (FogAndDistanceControl.w - FogAndDistanceControl.z), 0.0, 1.0);
        pixelSize = mix(1.0, 8.0, t); // Increase pixel size based on distance
    }
    vec2 pixelatedUV = applyPixelation(v_texcoord0, pixelSize);
    diffuse.rgb = texture2D(s_MatTexture, pixelatedUV).rgb;

    diffuse.rgb = mix(diffuse.rgb, FogColor.rgb, v_fog.a);
    gl_FragColor = diffuse;
}
