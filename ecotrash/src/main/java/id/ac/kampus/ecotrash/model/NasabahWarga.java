package id.ac.kampus.ecotrash.model;

/**
 * Entitas warga komunitas yang menyetorkan sampah pilah.
 * Menerapkan Enkapsulasi ketat untuk proteksi saldo kas dan akumulasi poin reward.
 */
public class NasabahWarga extends Pengguna {
    private String nomorRekening;
    private double saldoTabungan;
    private int poinReward;

    public NasabahWarga(String idPengguna, String namaLengkap, String noTelepon, String nomorRT, String nomorRekening) {
        super(idPengguna, namaLengkap, noTelepon, nomorRT);
        if (nomorRekening == null || nomorRekening.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor rekening wajib diisi.");
        }
        this.nomorRekening = nomorRekening;
        this.saldoTabungan = 0.0;
        this.poinReward = 0;
    }

    @Override
    public String peran() {
        return "Nasabah Warga RT " + nomorRT;
    }

    public void tambahSaldo(double nominal) {
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal penambahan saldo harus positif.");
        }
        this.saldoTabungan += nominal;
    }

    public boolean kurangiSaldo(double nominal) {
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal penarikan saldo harus positif.");
        }
        if (this.saldoTabungan >= nominal) {
            this.saldoTabungan -= nominal;
            return true;
        }
        return false;
    }

    public void tambahPoin(int poin) {
        if (poin <= 0) {
            throw new IllegalArgumentException("Penambahan poin harus lebih besar dari 0.");
        }
        this.poinReward += poin;
    }

    public boolean tukarPoin(int poin) {
        if (poin <= 0) {
            throw new IllegalArgumentException("Poin yang ditukar harus positif.");
        }
        if (this.poinReward >= poin) {
            this.poinReward -= poin;
            return true;
        }
        return false;
    }

    public String getNomorRekening() {
        return nomorRekening;
    }

    public double getSaldoTabungan() {
        return saldoTabungan;
    }

    public int getPoinReward() {
        return poinReward;
    }
}

