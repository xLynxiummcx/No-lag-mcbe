$input v_color0, v_fog, v_texcoord0

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>

SAMPLER2D(s_ParticleTexture, 0);

void main() {
    vec4 diffuse = texture2D(s_ParticleTexture, v_texcoord0);

    #if ALPHA_TEST
        if (diffuse.a < 0.5) {
            discard;
        }
    #endif

    // Combining multiplications into a single step
    diffuse.rgb *= v_color0.rgb;
    diffuse.a *= v_color0.a;

    // Apply fog only if necessary
    if (v_fog.a > 0.0) {
        diffuse.rgb = applyFog(diffuse.rgb, v_fog.rgb, v_fog.a);
    }

    // Ensure alpha is 1.0 if ALPHA_TEST is defined
    #if ALPHA_TEST
        diffuse.a = 1.0;
    #endif

    gl_FragColor = diffuse;
}
