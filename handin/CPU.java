package arch.sm213.machine.student;

import arch.sm213.machine.AbstractSM213CPU;
import machine.AbstractMainMemory;
import machine.RegisterSet;
import util.UnsignedByte;


/**
 * The Simple Machine CPU.
 * <p>
 * Simulate the execution of a single cycle of the Simple Machine SM213 CPU.
 */

public class CPU extends AbstractSM213CPU {

    /**
     * Create a new CPU.
     *
     * @param name   fully-qualified name of CPU implementation.
     * @param memory main memory used by CPU.
     */
    public CPU(String name, AbstractMainMemory memory) {
        super(name, memory);
    }

    /**
     * Fetch Stage of CPU Cycle.
     * Fetch instruction at address stored in "pc" register from memory into instruction register
     * and set "pc" to point to the next instruction to execute.
     * <p>
     * Input register:   pc.
     * Output registers: pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
     *
     * @throws MainMemory.InvalidAddressException when program counter contains an invalid memory address
     * @see AbstractSM213CPU for pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
     */
    @Override
    protected void fetch() throws MainMemory.InvalidAddressException {
        int pcVal = pc.get();
        UnsignedByte[] ins = mem.read(pcVal, 2);
        byte opCode = (byte) (ins[0].value() >>> 4);
        insOpCode.set(opCode);
        insOp0.set(ins[0].value() & 0x0f);
        insOp1.set(ins[1].value() >>> 4);
        insOp2.set(ins[1].value() & 0x0f);
        insOpImm.set(ins[1].value());
        pcVal += 2;
        switch (opCode) {
            case 0x0:
            case 0xb:
                long opExt = mem.readIntegerUnaligned(pcVal);
                pcVal += 4;
                insOpExt.set(opExt);
                instruction.set(ins[0].value() << 40 | ins[1].value() << 32 | opExt);
                break;
            default:
                insOpExt.set(0);
                instruction.set(ins[0].value() << 40 | ins[1].value() << 32);
        }
        pc.set(pcVal);
    }


    /**
     * Execution Stage of CPU Cycle.
     * Execute instruction that was fetched by Fetch stage.
     * <p>
     * Input state: pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt, reg, mem
     * Ouput state: pc, reg, mem
     *
     * @throws InvalidInstructionException                when instruction format is invalid.
     * @throws MachineHaltException                       when instruction is the HALT instruction.
     * @throws RegisterSet.InvalidRegisterNumberException when instruction references an invalid register (i.e, not 0-7).
     * @throws MainMemory.InvalidAddressException         when instruction references an invalid memory address.
     * @see AbstractSM213CPU for pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
     * @see MainMemory       for mem
     * @see machine.AbstractCPU      for reg
     */
    @Override
    protected void execute() throws InvalidInstructionException, MachineHaltException, RegisterSet.InvalidRegisterNumberException, MainMemory.InvalidAddressException {
        switch (insOpCode.get()) {
            case 0x0: // ld $i, d .............. 0d-- iiii iiii
                reg.set(insOp0.get(), insOpExt.get());
                break;
            case 0x1: // ld o(rs), rd .......... 1psd  (p = o / 4)
                reg.set(insOp2.get(), mem.readInteger(reg.get(insOp1.get()) + reg.get(insOp0.get()) * 4));
                // rd <- mem.readInteger(r[s] + 4*p)
                break;
            case 0x2: // ld (rs, ri, 4), rd .... 2sid
                int rI = reg.get(insOp0.get()) + (reg.get(insOp1.get()) * 4);
                reg.set(insOp2.get(), mem.readInteger(rI));
                // rd <- m[r[s] + 4*r[i]]
                break;
            case 0x3: // st rs, o(rd) .......... 3spd  (p = o / 4)
                mem.writeInteger(insOp1.get() << 2 + reg.get(insOp2.get()), reg.get(insOp0.get()));
                break;
            case 0x4: // st rs, (rd, ri, 4) .... 4sdi
                int wI = (reg.get(insOp2.get()) * 4) + reg.get(insOp1.get());
                mem.writeInteger(wI, (reg.get(insOp0.get())));
                // m[r[d] + r[i]*4] <- r[s]
                break;
            case 0x6: // ALU ................... 6-sd
                switch (insOp0.get()) {
                    case 0x0: // mov rs, rd ........ 60sd
                        reg.set(reg.get(insOp2.get()), reg.get(insOp1.get()));
                        break;
                    case 0x1: // add rs, rd ........ 61sd
                        int addition = reg.get(insOp1.get()) + reg.get(insOp2.get());
                        reg.set(insOp2.get(), addition);
                        break;
                    case 0x2: // and rs, rd ........ 62sd
                        int bitwiseWith = reg.get(insOp1.get()) & reg.get(insOp2.get());
                        reg.set(insOp2.get(), bitwiseWith);
                        break;
                    case 0x3: // inc rr ............ 63-r
                        int incr = reg.get(insOp2.get()) + 1;
                        reg.set(insOp2.get(), incr);
                        break;
                    case 0x4: // inca rr ........... 64-r
                        int inca = reg.get(insOp2.get()) + 4;
                        reg.set(insOp2.get(), inca);
                        // inca = increment address (+4)
                        break;
                    case 0x5: // dec rr ............ 65-r
                        int decr = reg.get(insOp2.get()) - 1;
                        reg.set(insOp2.get(), decr);
                        break;
                    case 0x6: // deca rr ........... 66-r
                        int deca = reg.get(insOp2.get()) - 4;
                        reg.set(insOp2.get(), deca);
                        break;
                    case 0x7: // not ............... 67-r
                        int negation = ~(reg.get(insOp2.get()));
                        reg.set(insOp2.get(), negation);
                        break;
                    default:
                        throw new InvalidInstructionException();
                }
                break;
            case 0x7: // sh? $i,rd ............. 7dii
                if (insOpImm.get() > 0) {
                    reg.set(insOp0.get(), ((long) reg.get(insOp0.get()) << insOpImm.get()));
                    // shl $i,rd ............. 7dii
                } else {
                    reg.set(insOp0.get(), (long) reg.get(insOp0.get()) >> -insOpImm.get());
                    // shr $i,rd ............. 7dii
                }
                break;
            case 0xf: // halt or nop ............. f?--
                if (insOp0.get() == 0)
                    // halt .......................... f0--
                    throw new MachineHaltException();
                else if (insOp0.getUnsigned() == 0xf)
                    // nop ........................... ff--
                    break;
                break;
            default:
                throw new InvalidInstructionException();
        }
    }
}
