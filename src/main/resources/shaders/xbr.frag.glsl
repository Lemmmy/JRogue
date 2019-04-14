#version 120

precision highp float;

#define XBR_SCALE 2.0
#define XBR_Y_WEIGHT 75.0
#define XBR_EQ_THRESHOLD 50.0
#define XBR_LV2_COEFFICIENT 2.0

varying vec4 v_texCoords[8];

uniform sampler2D u_texture;

uniform vec2 u_size;

const vec3 rgbw = vec3(14.352, 28.176, 5.472);

vec4 delta   = vec4(1.0 / XBR_SCALE, 1.0 / XBR_SCALE, 1.0 / XBR_SCALE, 1.0 / XBR_SCALE);
vec4 delta_l = vec4(0.5 / XBR_SCALE, 1.0 / XBR_SCALE, 0.5 / XBR_SCALE, 1.0 / XBR_SCALE);
vec4 delta_u = delta_l.yxwz;

const vec4 Ao = vec4(1.0, -1.0, -1.0, 1.0);
const vec4 Bo = vec4(1.0, 1.0, -1.0, -1.0);
const vec4 Co = vec4(1.5, 0.5, -0.5, 0.5);
const vec4 Ax = vec4(1.0, -1.0, -1.0, 1.0);
const vec4 Bx = vec4(0.5, 2.0, -0.5, -2.0);
const vec4 Cx = vec4(1.0, 1.0, -0.5, 0.0);
const vec4 Ay = vec4(1.0, -1.0, -1.0, 1.0);
const vec4 By = vec4(2.0, 0.5, -2.0, -0.5);
const vec4 Cy = vec4(2.0, 0.0, -1.0, 0.5);
const vec4 Ci = vec4(0.25, 0.25, 0.25, 0.25);

const vec3 Y = vec3(0.2126, 0.7152, 0.0722);

vec4 df(vec4 A, vec4 B) {
    return vec4(abs(A - B));
}

vec4 eq(vec4 A, vec4 B) {
    return step(df(A, B), vec4(XBR_EQ_THRESHOLD));
}

vec4 neq(vec4 A, vec4 B) {
    return vec4(1.0, 1.0, 1.0, 1.0) - eq(A, B);
}

vec4 wd(vec4 a, vec4 b, vec4 c, vec4 d, vec4 e, vec4 f, vec4 g, vec4 h) {
    return df(a, b) + df(a, c) + df(d, e) + df(d, f) + 4.0 * df(g, h);
}

float c_df(vec3 c1, vec3 c2) {
    vec3 df = abs(c1 - c2);
    return df.r + df.g + df.b;
}

void main() {
    vec4 edri, edr, edr_l, edr_u, px;
    vec4 irlv0, irlv1, irlv2l, irlv2u, block_3d;
    vec4 fx, fx_l, fx_u;

    vec2 fp = fract(v_texCoords[0].xy * u_size);

    vec3 A1 = texture2D(u_texture, v_texCoords[1].xw).xyz;
    vec3 B1 = texture2D(u_texture, v_texCoords[1].yw).xyz;
    vec3 C1 = texture2D(u_texture, v_texCoords[1].zw).xyz;
    vec3 A  = texture2D(u_texture, v_texCoords[2].xw).xyz;
    vec3 B  = texture2D(u_texture, v_texCoords[2].yw).xyz;
    vec3 C  = texture2D(u_texture, v_texCoords[2].zw).xyz;
    vec3 D  = texture2D(u_texture, v_texCoords[3].xw).xyz;
    vec3 E  = texture2D(u_texture, v_texCoords[3].yw).xyz;
    vec3 F  = texture2D(u_texture, v_texCoords[3].zw).xyz;
    vec3 G  = texture2D(u_texture, v_texCoords[4].xw).xyz;
    vec3 H  = texture2D(u_texture, v_texCoords[4].yw).xyz;
    vec3 I  = texture2D(u_texture, v_texCoords[4].zw).xyz;
    vec3 G5 = texture2D(u_texture, v_texCoords[5].xw).xyz;
    vec3 H5 = texture2D(u_texture, v_texCoords[5].yw).xyz;
    vec3 I5 = texture2D(u_texture, v_texCoords[5].zw).xyz;
    vec3 A0 = texture2D(u_texture, v_texCoords[6].xy).xyz;
    vec3 D0 = texture2D(u_texture, v_texCoords[6].xz).xyz;
    vec3 G0 = texture2D(u_texture, v_texCoords[6].xw).xyz;
    vec3 C4 = texture2D(u_texture, v_texCoords[7].xy).xyz;
    vec3 F4 = texture2D(u_texture, v_texCoords[7].xz).xyz;
    vec3 I4 = texture2D(u_texture, v_texCoords[7].xw).xyz;

    vec4 b  = vec4(dot(B, rgbw), dot(D, rgbw), dot(H, rgbw), dot(F, rgbw));
    vec4 c  = vec4(dot(C, rgbw), dot(A, rgbw), dot(G, rgbw), dot(I, rgbw));
    vec4 d  = b.yzwx;
    vec4 e  = vec4(dot(E, rgbw));
    vec4 f  = b.wxyz;
    vec4 g  = c.zwxy;
    vec4 h  = b.zwxy;
    vec4 i  = c.wxyz;

    float y_weight = XBR_Y_WEIGHT;

    vec4 i4 = vec4(dot(I4, rgbw), dot(C1, rgbw), dot(A0, rgbw), dot(G5, rgbw));
    vec4 i5 = vec4(dot(I5, rgbw), dot(C4, rgbw), dot(A1, rgbw), dot(G0, rgbw));
    vec4 h5 = vec4(dot(H5, rgbw), dot(F4, rgbw), dot(B1, rgbw), dot(D0, rgbw));
    vec4 f4;

    fx   = (Ao * fp.y + Bo * fp.x);
    fx_l = (Ax * fp.y + Bx * fp.x);
    fx_u = (Ay * fp.y + By * fp.x);

    irlv0 = vec4(notEqual(e, f)) * vec4(notEqual(e, h));
    irlv1 = (irlv0 * (neq(f, b) * neq(f, c) + neq(h, d) * neq(h, g) + eq(e, i) * (neq(f, f4) * neq(f, i4) + neq(h, h5) * neq(h, i5)) + eq(e, g) + eq(e, c)));

    irlv2l = vec4(notEqual(e,g)) * vec4(notEqual(d,g));
    irlv2u = vec4(notEqual(e,c)) * vec4(notEqual(b,c));

    vec4 fx45i = clamp((fx   + delta   -Co - Ci) / (2.0 * delta  ), 0.0, 1.0);
    vec4 fx45  = clamp((fx   + delta   -Co     ) / (2.0 * delta  ), 0.0, 1.0);
    vec4 fx30  = clamp((fx_l + delta_l -Cx     ) / (2.0 * delta_l), 0.0, 1.0);
    vec4 fx60  = clamp((fx_u + delta_u -Cy     ) / (2.0 * delta_u), 0.0, 1.0);

    vec4 wd1 = wd(e, c, g, i, h5, f4, h, f);
    vec4 wd2 = wd(h, d, i5, f, i4, b, e, i);

    edri  = step(wd1, wd2) * irlv0;
    edr   = step(wd1 + vec4(0.1, 0.1, 0.1, 0.1), wd2) * step(vec4(0.5, 0.5, 0.5, 0.5), irlv1);
    edr_l = step(XBR_LV2_COEFFICIENT * df(f, g), df(h, c)) * irlv2l * edr;
    edr_u = step(XBR_LV2_COEFFICIENT * df(h, c), df(f, g)) * irlv2u * edr;

    fx45 = edr * fx45;
    fx30 = edr_l * fx30;
    fx60 = edr_u * fx60;
    fx45i = edri * fx45i;

    px = step(df(e, f), df(e, h));

    vec4 maximos = max(max(fx30, fx60), fx45);

    vec3 res1 = E;
    res1 = mix(res1, mix(H, F, px.x), maximos.x);
    res1 = mix(res1, mix(B, D, px.z), maximos.z);

    vec3 res2 = E;
    res2 = mix(res2, mix(F, B, px.y), maximos.y);
    res2 = mix(res2, mix(D, H, px.w), maximos.w);

    gl_FragColor = vec4(mix(res1, res2, step(c_df(E, res1), c_df(E, res2))), 1.0);
}