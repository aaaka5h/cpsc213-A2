.pos 0x100
            ld  $2, r0
            ld  $a, r1
            ld  (r1, r0, 4), r2
            dec r0
            st  r2, (r1, r0, 4)
.pos 0x1000
a:          .long 9
            .long 7
            .long 2
            .long 5
            .long 6

