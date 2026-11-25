package id.ac.kampus.ecotrash.exception;

/**
 * Exception bisnis untuk pelanggaran aturan alur transaksi setoran
 * (misal: transaksi kosong, transaksi sudah selesai, atau sudah dibatalkan).
 */
public class TransaksiTidakValidException extends EcoTrashException {

    private static final long serialVersionUID = 1L;

    private final String kodeTransaksi;

    public TransaksiTidakValidException(String kodeTransaksi, String pesan) {
        super(String.format("Transaksi [%s] tidak valid: %s", kodeTransaksi, pesan));
        this.kodeTransaksi = kodeTransaksi;
    }

    public TransaksiTidakValidException(String kodeTransaksi, String pesan, Throwable sebab) {
        super(String.format("Transaksi [%s] tidak valid: %s", kodeTransaksi, pesan), sebab);
        this.kodeTransaksi = kodeTransaksi;
    }

    public String getKodeTransaksi() {
        return kodeTransaksi;
    }
}
