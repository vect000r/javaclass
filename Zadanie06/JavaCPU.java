import java.util.List;

public class JavaCPU implements CPU {
    private Memory ram;
    private int A;
    private int X;
    private int Y;

    @Override
    public void setRAM(Memory ram) {
        this.ram = ram;
    }

    @Override
    public void execute(List<String> code) {
        for (String operation : code) {
            if(operation.trim().isEmpty()) continue;
            if (operation.startsWith("IN")) {
                if (operation.equals("INX")) {
                    INX();
                } else if (operation.equals("INY")) {
                    INY();
                }
            } else if (operation.startsWith("LD")) {
                if (operation.startsWith("LDA")) {
                    LDA(operation);
                } else if (operation.startsWith("LDX")) {
                    LDX(operation);
                } else if (operation.startsWith("LDY")) {
                    LDY(operation);
                }
            } else if (operation.startsWith("STA")) {
                STA(operation);
            } else if (operation.startsWith("T")) {
                if (operation.equals("TAX")) {
                    TAX();
                } else if (operation.equals("TXA")) {
                    TXA();
                } else if (operation.equals("TXY")) {
                    TXY();
                } else if (operation.equals("TYX")) {
                    TYX();
                }
            } else if (operation.startsWith("ADC")) {
                ADC(operation);
            }
        }
    }

    private void INX() {
        X = X + 1;
    }

    private void INY() {
        Y = Y + 1;
    }

    private void LDA(String operation) {
        String[] parts = operation.split(" ");
        if (parts[1].startsWith("#")) {
            A = Integer.parseInt(parts[1].substring(1));
        } else {
            A = ram.get(Integer.parseInt(parts[1]));
        }
    }

    private void LDX(String operation) {
        String[] parts = operation.split(" ");
        if (parts.length == 2) {
            if (parts[1].startsWith("#")) {
                X = Integer.parseInt(parts[1].substring(1));
            } else if (parts[1].contains(",")) {
                String[] subParts = parts[1].split(",");
                int baseAddr = Integer.parseInt(subParts[0]);
                if (subParts[1].equals("Y")) {
                    X = ram.get(baseAddr + Y);
                }
            } else {
                X = ram.get(Integer.parseInt(parts[1]));
            }
        }
    }

    private void LDY(String operation) {
        String[] parts = operation.split(" ");
        if (parts[1].startsWith("#")) {
            Y = Integer.parseInt(parts[1].substring(1));
        } else {
            Y = ram.get(Integer.parseInt(parts[1]));
        }
    }

    private void STA(String operation) {
        String[] parts = operation.split(" ");
        ram.set(Integer.parseInt(parts[1]), (short) A);
    }

    private void TAX() {
        X = A;
    }

    private void TXA() {
        A = X;
    }

    private void TXY() {
        Y = X;
    }

    private void TYX() {
        X = Y;
    }

    private void ADC(String operation) {
        String[] parts = operation.split(" ");
        if (parts[1].startsWith("#")) {
            A = A + Integer.parseInt(parts[1].substring(1));
        } else if (parts[1].equals("X")) {
            A = A + X;
        } else if (parts[1].equals("Y")) {
            A = A + Y;
        } else {
            A = A + ram.get(Integer.parseInt(parts[1]));
        }
    }
}
