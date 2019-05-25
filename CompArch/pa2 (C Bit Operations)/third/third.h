unsigned short get(unsigned short x, unsigned short n);
unsigned short comp(unsigned short x, unsigned short n);
unsigned short set(unsigned short x, unsigned short n, unsigned short v);


unsigned short get(unsigned short x, unsigned short n){
  return (x >> n) & 1;
}

unsigned short comp(unsigned short x, unsigned short n){
  unsigned short complement_nth_bit = get(x, n) == 0 ? 1 : 0;
  return set(x, n, complement_nth_bit);
}

unsigned short set(unsigned short x, unsigned short n, unsigned short v){
  return v==1 ? (1 << n) | x : ~(1 << n) & x;
}
