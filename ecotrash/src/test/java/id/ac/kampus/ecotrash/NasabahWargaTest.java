package id.ac.kampus.ecotrash;

import id.ac.kampus.ecotrash.exception.SaldoTidakCukupException;
import id.ac.kampus.ecotrash.model.NasabahWarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pengujian unit untuk NasabahWarga dan teknik partisi/nilai batas pada kurangiSaldo().
 */
public class NasabahWargaTest {

    private NasabahWarga nasabah;

    @BeforeEach
    void siapkan() {
        nasabah = new NasabahWarga("NSB01", "Ibu Siti Aminah", "081298765432", "04", "REK-04-012");
        nasabah.tambahSaldo(50000.0);
    }

    @Test
    @DisplayName("TC-01 Batas bawah tidak valid: kurangiSaldo(0) -> IllegalArgumentException")
    void kurangiSaldoBatasBawahNol() {
        assertThrows(IllegalArgumentException.class, () -> nasabah.kurangiSaldo(0.0));
        assertEquals(50000.0, nasabah.getSaldoTabungan());
    }

    @Test
    @DisplayName("TC-02 Partisi tidak valid: kurangiSaldo(-5000) -> IllegalArgumentException")
    void kurangiSaldoPartisiNegatif() {
        assertThrows(IllegalArgumentException.class, () -> nasabah.kurangiSaldo(-5000.0));
        assertEquals(50000.0, nasabah.getSaldoTabungan());
    }

    @Test
    @DisplayName("TC-03 Batas bawah valid: kurangiSaldo(1) dari 50000 -> sisa 49999")
    void kurangiSaldoBatasBawahValid() throws SaldoTidakCukupException {
        nasabah.kurangiSaldo(1.0);
        assertEquals(49999.0, nasabah.getSaldoTabungan());
    }

    @Test
    @DisplayName("TC-04 Batas atas valid: kurangiSaldo(50000) dari 50000 -> sisa 0")
    void kurangiSaldoBatasAtasValid() throws SaldoTidakCukupException {
        nasabah.kurangiSaldo(50000.0);
        assertEquals(0.0, nasabah.getSaldoTabungan());
    }

    @Test
    @DisplayName("TC-05 Batas atas tidak valid: kurangiSaldo(50001) -> SaldoTidakCukupException")
    void kurangiSaldoMelebihiSaldo() {
        SaldoTidakCukupException e = assertThrows(SaldoTidakCukupException.class,
                () -> nasabah.kurangiSaldo(50001.0));
        assertAll(
                () -> assertEquals("REK-04-012", e.getNomorRekening()),
                () -> assertEquals(50001.0, e.getDiminta()),
                () -> assertEquals(50000.0, e.getTersedia()),
                () -> assertEquals(1.0, e.getKekurangan()),
                () -> assertEquals(50000.0, nasabah.getSaldoTabungan(), "saldo tidak boleh berubah"));
    }

    @Test
    @DisplayName("TC-06 Validasi enkapsulasi: tambahSaldo/tambahPoin negatif & ID kosong ditolak")
    void validasiAturanBisnisNasabah() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> nasabah.tambahSaldo(-1000.0)),
                () -> assertThrows(IllegalArgumentException.class, () -> nasabah.tambahPoin(0)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new NasabahWarga("", "Anonim", "0811", "04", "REK-00")));
    }
}
