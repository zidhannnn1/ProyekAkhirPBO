package id.ac.kampus.ecotrash.exception;

/**
 * Exception bisnis ketika entitas yang dicari berdasarkan ID/kode tidak ditemukan.
 */
public class DataTidakDitemukanException extends EcoTrashException {

    private final String jenisData;
    private final String kunci;

    public DataTidakDitemukanException(String jenisData, String kunci) {
        super(String.format("%s dengan ID/kode '%s' tidak ditemukan.", jenisData, kunci));
        this.jenisData = jenisData;
        this.kunci = kunci;
    }

    public String getJenisData() {
        return jenisData;
    }

    public String getKunci() {
        return kunci;
    }
}
