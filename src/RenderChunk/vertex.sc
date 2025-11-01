$input a_color0, a_position, a_texcoord0, a_texcoord1
#ifdef INSTANCING
    $input i_data1, i_data2, i_data3
#endif
$output v_color0, v_fog, v_texcoord0, v_lightmapUV, v_position

#include <bgfx_shader.sh>
#include <utils/fog.h>

uniform vec4 RenderChunkFogAlpha;
uniform vec4 FogAndDistanceControl;
uniform vec4 ViewPositionAndTime;
uniform vec4 FogColor;

float smoothfog(vec2 nearFar,float dist){
return smoothstep(nearFar.x,nearFar.y,dist);
}
void main() {
  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data1, i_data2, i_data3, vec4(0.0, 0.0, 0.0, 1.0));
  #else
    mat4 model = u_model[0];
  #endif
  vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;

  vec4 color;
  #ifdef RENDER_AS_BILLBOARDS
    worldPos += vec3(0.5, 0.5, 0.5);
    vec3 viewDir = normalize(worldPos - ViewPositionAndTime.xyz);
    vec3 boardPlane = normalize(vec3(viewDir.z, 0.0, -viewDir.x));
    worldPos -= (viewDir.yzx*boardPlane.zxy - viewDir.zxy*boardPlane.yzx)*(a_color0.z - 0.5) + boardPlane*(a_color0.x - 0.5);
    color = vec4_splat(1.0);
  #else
    color = a_color0;
  #endif

  vec3 modelCamPos = ViewPositionAndTime.xyz - worldPos;
  float cameraDist = length(modelCamPos);
  float relativeDepth = cameraDist / FogAndDistanceControl.w;
  float relativeDist  = cameraDist / FogAndDistanceControl.z;
  
  vec4 fogColor;
       fogColor.rgb = FogColor.rgb;
       fogColor.a   = smoothfog(FogAndDistanceControl.xy,relativeDist);
       
 
  //Newb X Legacy
 // 0-255 = first 4 bits for y, remaining for x
  float uvx16 = a_texcoord1.x * 15.9375; // 255/16
  vec2 uv1 = vec2(fract(uvx16), floor(uvx16)*0.0625); // (a&15, a>>4)
  
  #ifdef TRANSPARENT
    if (a_color0.a < 0.95) {
      color.a = mix(a_color0.a, 1.0, clamp(relativeDepth, 0.0, 1.0));
    };
  #endif

  v_texcoord0 = a_texcoord0;
  v_lightmapUV = uv1;
  v_color0 = color;
  v_fog = fogColor;
  v_position = a_position;
  gl_Position = mul(u_viewProj, vec4(worldPos, 1.0));
}
