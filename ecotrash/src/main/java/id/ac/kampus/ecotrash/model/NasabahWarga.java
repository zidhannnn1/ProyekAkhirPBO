package id.ac.kampus.ecotrash.model;

import id.ac.kampus.ecotrash.exception.SaldoTidakCukupException;

/**
 * Warga RT yang menabung sampah terpilah ke bank sampah.
 * Memiliki saldo tabungan dan poin reward yang bermutasi melalui transaksi.
 */
public class NasabahWarga extends Pengguna {

    private final String nomorRekening;
    private double saldoTabungan;
    private int poinReward;

    public NasabahWarga(String idPengguna, String namaLengkap,
                        String noTelepon, String nomorRT,
                        String nomorRekening) {
        super(idPengguna, namaLengkap, noTelepon, nomorRT);
        if (nomorRekening == null || nomorRekening.isBlank()) {
            throw new IllegalArgumentException("nomorRekening tidak boleh kosong");
        }
        this.nomorRekening = nomorRekening;
        this.saldoTabungan = 0.0;
        this.poinReward = 0;
    }

    public void tambahSaldo(double nominal) {
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal tambah saldo harus positif");
        }
        this.saldoTabungan += nominal;
    }

    /**
     * Mengurangi saldo tabungan nasabah saat penarikan kas atau pembatalan setoran.
     * Melempar IllegalArgumentException bila nominal <= 0 (kesalahan argumen),
     * dan melempar SaldoTidakCukupException bila saldo tidak mencukupi (kesalahan bisnis).
     */
    public void kurangiSaldo(double nominal) throws SaldoTidakCukupException {
        if (nominal <= 0) {
            throw new IllegalArgumentException("Nominal kurangi saldo harus positif");
        }
        if (nominal > saldoTabungan) {
            throw new SaldoTidakCukupException(nomorRekening, nominal, saldoTabungan);
        }
        this.saldoTabungan -= nominal;
    }

    public void tambahPoin(int poin) {
        if (poin <= 0) {
            throw new IllegalArgumentException("Poin yang ditambahkan harus positif");
        }
        this.poinReward += poin;
    }

    /** @return false jika poin tidak mencukupi */
    public boolean tukarPoin(int poin) {
        if (poin <= 0) {
            throw new IllegalArgumentException("Poin yang ditukar harus positif");
        }
        if (poin > poinReward) {
            return false;
        }
        this.poinReward -= poin;
        return true;
    }

    @Override
    public String peran() {
        return "Nasabah Warga RT " + nomorRT;
    }

    public String getNomorRekening() { return nomorRekening; }
    public double getSaldoTabungan() { return saldoTabungan; }
    public int getPoinReward() { return poinReward; }
}
