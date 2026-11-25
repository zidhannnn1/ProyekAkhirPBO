package id.ac.kampus.ecotrash.exception;

/**
 * Exception bisnis ketika saldo tabungan nasabah tidak mencukupi
 * untuk melakukan pencairan tunai atau pengembalian saldo.
 */
public class SaldoTidakCukupException extends EcoTrashException {

    private static final long serialVersionUID = 1L;

    private final String nomorRekening;
    private final double diminta;
    private final double tersedia;

    public SaldoTidakCukupException(String nomorRekening, double diminta, double tersedia) {
        super(String.format("Saldo rekening %s tidak cukup: diminta Rp %,.0f, tersedia Rp %,.0f",
                nomorRekening, diminta, tersedia));
        this.nomorRekening = nomorRekening;
        this.diminta = diminta;
        this.tersedia = tersedia;
    }

    public String getNomorRekening() {
        return nomorRekening;
    }

    public double getDiminta() {
        return diminta;
    }

    public double getTersedia() {
        return tersedia;
    }

    public double getKekurangan() {
        return Math.max(0.0, diminta - tersedia);
    }
}
