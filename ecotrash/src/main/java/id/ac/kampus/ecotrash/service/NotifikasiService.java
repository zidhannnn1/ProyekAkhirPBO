package id.ac.kampus.ecotrash.service;

/**
 * Abstraksi pengiriman notifikasi ke nasabah.
 * Memungkinkan substitusi implementasi (SMS, WhatsApp, console) tanpa
 * mengubah kode service layer yang memakainya.
 */
public interface NotifikasiService {
    void kirimNotifikasi(String noTelepon, String pesan);
}
