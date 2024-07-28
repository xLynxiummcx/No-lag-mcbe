$input v_color0, v_fog, v_texcoord0, v_lightmapUV

#include <bgfx_shader.sh>

uniform vec4 FogColor;

SAMPLER2D(s_MatTexture, 0);
SAMPLER2D(s_SeasonsTexture, 1);
SAMPLER2D(s_LightMapTexture, 2);

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

    // Calculate motion blur effect
    vec4 currentPos = gl_FragCoord; // Current screen position
    vec4 prevPos = currentPos + u_prevWorldPosOffset; // Estimated previous position
    vec2 velocity = (currentPos.xy - prevPos.xy) * 0.1; // Calculate velocity and scale it down

    // Apply motion blur based on velocity
    vec4 motionBlurColor = vec4(0.0);
    int samples = 5;
    for (int i = 0; i < samples; ++i) {
        vec2 offset = velocity * (float(i) / float(samples - 1));
        motionBlurColor += texture2D(s_MatTexture, v_texcoord0 + offset);
    }
    motionBlurColor /= float(samples);

    // Mix the motion blur color with the original diffuse color
    diffuse.rgb = mix(diffuse.rgb, motionBlurColor.rgb, 0.5); // Adjust the blend factor as needed

    diffuse.rgb = mix(diffuse.rgb, FogColor.rgb, v_fog.a);
    gl_FragColor = diffuse;
}
