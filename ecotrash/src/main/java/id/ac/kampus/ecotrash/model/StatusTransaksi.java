package id.ac.kampus.ecotrash.model;

/**
 * Siklus status resmi untuk transaksi setoran sampah.
 * Menggantikan String mentah pada v3 untuk mencegah typo dan status tidak sah.
 */
public enum StatusTransaksi {
    DIPROSES,
    SELESAI,
    DIBATALKAN
}
