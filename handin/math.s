.pos 0x100
                ld $f, r0                # r0 = address of f
                ld $m, r1                # r1 = address of m
                ld $0x04, r2             # r2 = 0x04
                ld $0xffffffff, r3       # r3 = 0x0f
                ld 0x0(r0), r4           # r4 = value of f
                ld $0x01, r5             # r5 = 0x01   

                add r4, r5               # (f + 1)
                add r5, r2               # ((f + 1) + 4)
                shl $2, r2               # (((f + 1) + 4) << 2)
                and r4, r2               # ((((f + 1) + 4) << 2) & f)
                shr $4, r2               # (((((f + 1) + 4) << 2) & f) / 16)
                st r2, 0x0(r1)           # m = (((((f + 1) + 4) << 2) & f) / 16)
                halt                     # halt        


.pos 0x1000 
m:              .long 0xffffffff         # m = -1
.pos 0x2000
f:              .long 0xffffffff         # f = -1