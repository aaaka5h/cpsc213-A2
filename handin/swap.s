
.pos 0x100
                ld $orig, r0             # r0 = address of orig
                ld $array, r1            # r1 = address of array
                ld 16(r1), r2            # r2 = value of array[4]
                ld 12(r1), r3            # r3 = value of array[3]
                st r2, (r0)              # orig = array[4]
                st r3, 16(r1)            # array[4] = array[3]
                ld (r0), r0              # r0 = value of orig
                st r0, 12(r1)            # array[3] = orig
                halt                     # halt
                

.pos 0x1000 
orig:           .long 0xffffffff         # orig
.pos 0x2000
array:          .long 0x00000001         # array[0]
                .long 0x00000002         # array[1]
                .long 0x00000003         # array[2]
                .long 0x00000004         # array[3]
                .long 0x00000005         # array[4]
                .long 0x00000006         # array[5]
                .long 0x00000007         # array[6]


