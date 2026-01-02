$input v_texcoord0, v_color0, v_fog, v_lightmapUV, v_position

#include <bgfx_shader.sh>

#ifndef DEPTH_ONLY_OPAQUE
  SAMPLER2D_AUTOREG(s_LightMapTexture);
  SAMPLER2D_AUTOREG(s_MatTexture);

  #if defined(SEASONS) && (defined(ALPHA_TEST) || defined(OPAQUE))
    SAMPLER2D_AUTOREG(s_SeasonsTexture);
  #endif
#endif

void main() {
  #ifndef DEPTH_ONLY_OPAQUE
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);

    #ifdef ALPHA_TEST
     if (!gl_FrontFacing || (diffuse.a < 0.6)) {
      discard;
    }
    #endif

vec4 vertexColor = v_color0;

#if defined(REMOVE_AO)
vertexColor.rgb = vertexColor.rgb / max(max(vertexColor.r,vertexColor.g)vertexColor.b);
#endif

    #if defined(SEASONS) && (defined(ALPHA_TEST) || defined(OPAQUE))
      diffuse.rgb *= mix(vec3_splat(1.0), 2.0 * texture2D(s_SeasonsTexture, v_color0.xy).rgb, v_color0.y);
      diffuse.rgb *= v_color0.aaa;
    #else
      diffuse *= vertexColor;
    #endif
 
 #if !defined(NIGHT_VISION)
    diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).xyz;
 #endif
  
  #if defined(AO_DEBUG)
     diffuse.rgb = v_color0.ggg;
  #endif
  
  #if defined(NO_TEXTURE)
  diffuse.rgb = vec3_splat(1.0);
  #endif
  
   #if defined(ENABLE_FOG)
    diffuse.rgb = mix(diffuse.rgb,v_fog.rgb,v_fog.a);
  #endif
  
    gl_FragColor = diffuse;
  #else
    gl_FragColor = vec4_splat(0.0);
  #endif
}

