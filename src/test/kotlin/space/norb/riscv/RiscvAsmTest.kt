package space.norb.riscv

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RiscvAsmTest {
    @Test
    fun rendersRv32imAndPseudoAssembly() {
        val loop = labelRef("loop")
        val done = labelRef("done")

        val program = riscv {
            text()
            global("main")
            label("main")
            li(t0, 10)
            li(t1, 1)
            label(loop)
            mul(t1, t1, t0)
            addi(t0, t0, -1)
            bnez(t0, loop)
            mv(a0, t1)
            j(done)
            label(done)
            ret()
        }

        assertEquals(
            """
            |    .section .text
            |    .globl main
            |main:
            |    li t0, 10
            |    li t1, 1
            |loop:
            |    mul t1, t1, t0
            |    addi t0, t0, -1
            |    bnez t0, loop
            |    mv a0, t1
            |    j done
            |done:
            |    ret
            |
            """.trimMargin(),
            program.render(),
        )
    }

    @Test
    fun supportsMemoryOperandsAndDirectives() {
        val program = riscv {
            data()
            align(2)
            label("numbers")
            word(1, 2, 3)
            text()
            lw(a0, sp[0])
            sw(a0, sp[4])
            la(a1, "numbers")
            asciz("hello\n")
        }

        assertEquals(
            """
            |    .section .data
            |    .align 2
            |numbers:
            |    .word 1, 2, 3
            |    .section .text
            |    lw a0, 0(sp)
            |    sw a0, 4(sp)
            |    la a1, numbers
            |    .asciz "hello\n"
            |
            """.trimMargin(),
            program.render(),
        )
    }

    @Test
    fun rejectsInstructionsOutsideTargetExtensions() {
        assertFailsWith<IllegalArgumentException> {
            riscv(RiscvTarget.RV32I) {
                mul(t0, t1, t2)
            }
        }
    }

    @Test
    fun validatesRv32ShiftAmounts() {
        assertFailsWith<IllegalArgumentException> {
            riscv {
                slli(t0, t1, 32)
            }
        }
    }
}
