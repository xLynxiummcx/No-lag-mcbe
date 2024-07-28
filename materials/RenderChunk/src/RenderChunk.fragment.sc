$input v_color0, v_fog, v_texcoord0, v_lightmapUV, v_prevTexcoord0
#include <bgfx_shader.sh>
uniform vec4 FogColor;


float calculateMotionBlurAmount(vec2 currTexcoord, vec2 prevTexcoord) {
    // Calculate motion blur amount based on position difference
    // For example:
    float distance = length(currTexcoord - prevTexcoord);
    return smoothstep(0.0, 1.0, distance * 0.1);
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
    diffuse.rgb *= mix(vec3(1.0, 1.0, 1.0), texture2D(s_SeasonsTexture, v_color0.xy).rgb * 2.0, v_color0.b);
    diffuse.rgb *= v_color0.aaa;
    #else
    diffuse *= v_color0;
    #endif
    #endif
    #ifndef TRANSPARENT
    diffuse.a = 1.0;
    #endif
    diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).rgb;
    
    // Motion blur
    vec4 prevDiffuse = texture2D(s_MatTexture, v_prevTexcoord0);
    float motionBlurAmount = calculateMotionBlurAmount(v_texcoord0, v_prevTexcoord0);
    diffuse = mix(diffuse, prevDiffuse, motionBlurAmount);
    
    diffuse.rgb = mix(diffuse.rgb, FogColor.rgb, v_fog.a);
    gl_FragColor = diffuse;
}
