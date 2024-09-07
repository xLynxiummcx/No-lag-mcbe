$input v_color0, v_fog, v_texcoord0, v_lightmapUV

#include <bgfx_shader.sh>

uniform vec4 FogColor;

SAMPLER2D(s_MatTexture, 0);
SAMPLER2D(s_SeasonsTexture, 1);
SAMPLER2D(s_LightMapTexture, 2);

void main() {
    vec4 diffuse = vec4(1.0);  // Initialize

#if !defined(DEPTH_ONLY_OPAQUE) && !defined(DEPTH_ONLY)
    diffuse = texture2D(s_MatTexture, v_texcoord0);

    #if defined(ALPHA_TEST)
        if (diffuse.a < 0.5) discard;
    #endif

    #if defined(SEASONS) && (defined(OPAQUE) || defined(ALPHA_TEST))
        vec3 seasonColor = texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0;
        diffuse.rgb *= mix(vec3(1.0), seasonColor, v_color0.b) * v_color0.aaa;
    #else
        diffuse.rgb *= v_color0.rgb;
    #endif
#endif

    #ifndef TRANSPARENT
    diffuse.a = 1.0;  // Ensure full opacity if not in TRANSPARENT mode
    #endif

    // Optimize light map lookup
    vec3 lightMapColor = texture2D(s_LightMapTexture, v_lightmapUV).rgb;
    diffuse.rgb *= lightMapColor;

    // Apply fog based on fog alpha value
    diffuse.rgb = mix(diffuse.rgb, FogColor.rgb, v_fog.a);

    // Final color output
    gl_FragColor = diffuse;
}
