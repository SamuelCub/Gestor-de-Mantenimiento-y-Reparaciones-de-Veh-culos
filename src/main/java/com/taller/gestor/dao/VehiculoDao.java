package com.taller.gestor.dao;

import com.taller.gestor.dto.VehiculoDetalleDto;
import com.taller.gestor.model.Vehiculo;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Acceso a datos para la entidad Vehiculo mediante JDBC nativo.
 */
@Repository
public class VehiculoDao {

    private final DataSource dataSource;

    public VehiculoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean existsByMatricula(String matricula) {
        String sql = "SELECT 1 FROM vehiculos WHERE matricula = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de vehículo: " + e.getMessage(), e);
        }
    }

    public Optional<VehiculoDetalleDto> findDetalleByMatricula(String matricula) {
        String sql = """
            SELECT v.matricula, v.marca, v.modelo, v.anio, v.cliente_id,
                   c.nombre AS cliente_nombre, c.telefono AS cliente_telefono, c.email AS cliente_email
            FROM vehiculos v
            INNER JOIN clientes c ON v.cliente_id = c.id
            WHERE UPPER(v.matricula) = UPPER(?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VehiculoDetalleDto dto = new VehiculoDetalleDto();
                    dto.setMatricula(rs.getString("matricula"));
                    dto.setMarca(rs.getString("marca"));
                    dto.setModelo(rs.getString("modelo"));
                    dto.setAnio(rs.getInt("anio"));
                    dto.setClienteId(rs.getInt("cliente_id"));
                    dto.setClienteNombre(rs.getString("cliente_nombre"));
                    dto.setClienteTelefono(rs.getString("cliente_telefono"));
                    dto.setClienteEmail(rs.getString("cliente_email"));
                    return Optional.of(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar vehículo por matrícula: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Vehiculo create(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (matricula, marca, modelo, anio, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehiculo.getMatricula().trim().toUpperCase());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setInt(5, vehiculo.getClienteId());

            ps.executeUpdate();
            return vehiculo;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar vehículo: " + e.getMessage(), e);
        }
    }
}
