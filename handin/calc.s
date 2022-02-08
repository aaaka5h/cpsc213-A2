.pos 0x100
                ld $i, r0                # r0 = & i
                ld $p, r1                # r1 = & p
                ld $m, r2                # r2 = & m
                ld $q, r3                # r3 = & q
                ld 0x0(r0), r4           # r4 = i
                ld (r3, r4, 4), r5       # r5 = q[i]
                ld $0x03, r6             # r6 = 3
                add r4, r6               # r6 = i + 3
                ld (r3, r6, 4), r7       # r7 = q[i + 3]
                add r5, r7               # r7 = q[i + 3] + q[i]
                st r7, 0x0(r2)           # m = q[i + 3] + q[i]
                ld $0xf, r0              # r0 = & 0xf
                and r7, r0               # r0 = m & 0xf
                st r0, 0x0(r1)           # p = m & 0xf
                halt                     # halt 



.pos 0x1000
q:              .long 0xffffffff         # q[0] = -1
                .long 0x00000005         # q[1] = 5
                .long 0xffffff80         # q[2] = -128
                .long 0xffffffff         # q[3] = -1
                .long 0x00000005         # q[4] = 5
                .long 0xfffffffd         # q[5] = -3

.pos 0x2000
i:              .long 0x00000001         # i = 1
p:              .long 0x00000004         # p = 4
m:              .long 0x00000006         # m = 6