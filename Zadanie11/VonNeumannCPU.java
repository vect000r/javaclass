public class VonNeumannCPU implements CPU {
    private Memory ram;
    private int A;
    private int X;
    private int Y;
    private int PC;

    @Override
    public void setRAM(Memory ram) {
        this.ram = ram;
    }

    @Override
    public void execute(int address) {
        PC = address;
        while (true) {
            int opcode = ram.get(PC) & 0xFF;
            PC++;
            int arg = ram.get(PC) & 0xFF;
            PC++;

            switch (opcode) {
                case 0x04: // INX
                    X = (X + 1) & 0xFF;
                    break;
                case 0x05: // INY
                    Y = (Y + 1) & 0xFF;
                    break;
                case 0x08: // LDA #V
                    A = arg;
                    break;
                case 0x09: // LDA V
                    A = ram.get(arg) & 0xFF;
                    break;
                case 0x0A: // LDA V,Y
                    A = ram.get((arg + Y) & 0xFF) & 0xFF;
                    break;
                case 0x0C: // LDX #V
                    X = arg;
                    break;
                case 0x0D: // LDX V
                    X = ram.get(arg) & 0xFF;
                    break;
                case 0x0E: // LDX V,Y
                    X = ram.get((arg + Y) & 0xFF) & 0xFF;
                    break;
                case 0x10: // LDY #V
                    Y = arg;
                    break;
                case 0x11: // LDY V
                    Y = ram.get(arg) & 0xFF;
                    break;
                case 0x14: // STA V
                    ram.set(arg, (short) A);
                    break;
                case 0x18: // TAX
                    X = A & 0xFF;
                    break;
                case 0x19: // TXA
                    A = X & 0xFF;
                    break;
                case 0x1A: // TXY
                    Y = X & 0xFF;
                    break;
                case 0x1B: // TYX
                    X = Y & 0xFF;
                    break;
                case 0x1C: // ADC #V
                    A = (A + arg) & 0xFF;
                    break;
                case 0x1D: // ADC V
                    A = (A + ram.get(arg)) & 0xFF;
                    break;
                case 0x1E: // ADC X
                    A = (A + X) & 0xFF;
                    break;
                case 0x1F: // ADC Y
                    A = (A + Y) & 0xFF;
                    break;
                case 0x80: // JMP #V
                    PC = arg;
                    break;
                case 0x84: // CMP A,X
                    if (A == X) PC += 2;
                    break;
                case 0x85: // CMP A,Y
                    if (A == Y) PC += 2;
                    break;
                case 0x88: // CMP A,#V
                    if (A == arg) PC += 2;
                    break;
                case 0x89: // CMP X,#V
                    if (X == arg) PC += 2;
                    break;
                case 0x8A: // CMP Y,#V
                    if (Y == arg) PC += 2;
                    break;
                case 0xFE: // NOP
                    break;
                case 0xFF: // END
                    return;
                default:
                    break;
            }
        }
    }
}
