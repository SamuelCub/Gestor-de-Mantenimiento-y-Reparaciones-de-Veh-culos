package com.taller.gestor.dao;

import com.taller.gestor.dto.ReparacionDetalleDto;
import com.taller.gestor.model.EstadoReparacion;
import com.taller.gestor.model.Reparacion;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos para la entidad Reparacion mediante JDBC nativo.
 */
@Repository
public class ReparacionDao {

    private final DataSource dataSource;

    public ReparacionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ReparacionDetalleDto> findAllDetalladas() {
        String sql = """
            SELECT r.id, r.vehiculo_matricula, r.descripcion, r.coste, r.estado, r.fecha_ingreso,
                   v.marca, v.modelo, v.anio,
                   c.nombre AS cliente_nombre, c.telefono AS cliente_telefono, c.email AS cliente_email
            FROM reparaciones r
            INNER JOIN vehiculos v ON r.vehiculo_matricula = v.matricula
            INNER JOIN clientes c ON v.cliente_id = c.id
            ORDER BY r.fecha_ingreso DESC, r.id DESC
        """;

        List<ReparacionDetalleDto> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReparacionDetalleDto dto = new ReparacionDetalleDto();
                dto.setId(rs.getInt("id"));
                dto.setMatricula(rs.getString("vehiculo_matricula"));
                dto.setMarca(rs.getString("marca"));
                dto.setModelo(rs.getString("modelo"));
                dto.setAnio(rs.getInt("anio"));
                dto.setClienteNombre(rs.getString("cliente_nombre"));
                dto.setClienteTelefono(rs.getString("cliente_telefono"));
                dto.setClienteEmail(rs.getString("cliente_email"));
                dto.setDescripcion(rs.getString("descripcion"));
                dto.setCoste(rs.getBigDecimal("coste"));
                dto.setEstado(rs.getString("estado"));
                Date sqlDate = rs.getDate("fecha_ingreso");
                if (sqlDate != null) {
                    dto.setFechaIngreso(sqlDate.toLocalDate());
                }
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar lista de reparaciones: " + e.getMessage(), e);
        }
        return list;
    }

    public Optional<Reparacion> findById(int id) {
        String sql = "SELECT id, vehiculo_matricula, descripcion, coste, estado, fecha_ingreso FROM reparaciones WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reparacion r = new Reparacion();
                    r.setId(rs.getInt("id"));
                    r.setVehiculoMatricula(rs.getString("vehiculo_matricula"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setCoste(rs.getBigDecimal("coste"));
                    r.setEstado(EstadoReparacion.valueOf(rs.getString("estado")));
                    Date d = rs.getDate("fecha_ingreso");
                    if (d != null) {
                        r.setFechaIngreso(d.toLocalDate());
                    }
                    return Optional.of(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar reparación por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Reparacion create(Reparacion reparacion) {
        String sql = "INSERT INTO reparaciones (vehiculo_matricula, descripcion, coste, estado, fecha_ingreso) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reparacion.getVehiculoMatricula().trim().toUpperCase());
            ps.setString(2, reparacion.getDescripcion());
            ps.setBigDecimal(3, reparacion.getCoste());
            ps.setString(4, reparacion.getEstado().name());
            ps.setDate(5, Date.valueOf(reparacion.getFechaIngreso()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al insertar la reparación, ninguna fila afectada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reparacion.setId(generatedKeys.getInt(1));
                }
            }
            return reparacion;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar reparación: " + e.getMessage(), e);
        }
    }

    public boolean updateEstado(int id, EstadoReparacion nuevoEstado) {
        String sql = "UPDATE reparaciones SET estado = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado de la reparación: " + e.getMessage(), e);
        }
    }
}
