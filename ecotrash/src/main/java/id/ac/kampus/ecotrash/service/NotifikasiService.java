package id.ac.kampus.ecotrash.service;

/**
 * Interface abstraksi untuk pengiriman notifikasi/konfirmasi setoran kepada nasabah.
 */
public interface NotifikasiService {
    void kirimNotifikasi(String noTelepon, String pesan);
}

