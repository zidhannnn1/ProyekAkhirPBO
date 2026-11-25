package id.ac.kampus.ecotrash.exception;

/**
 * Induk checked exception untuk seluruh kesalahan bisnis EcoTrash
 * yang wajar terjadi dan dapat ditangani/dipulihkan oleh pemanggil.
 */
public class EcoTrashException extends Exception {

    public EcoTrashException(String pesan) {
        super(pesan);
    }

    public EcoTrashException(String pesan, Throwable sebab) {
        super(pesan, sebab);
    }
}
