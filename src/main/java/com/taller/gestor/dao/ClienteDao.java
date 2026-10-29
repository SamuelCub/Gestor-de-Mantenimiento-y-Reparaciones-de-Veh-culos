package com.taller.gestor.dao;

import com.taller.gestor.model.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * Acceso a datos para la entidad Cliente mediante JDBC nativo y PreparedStatement.
 */
@Repository
public class ClienteDao {

    private final DataSource dataSource;

    public ClienteDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Cliente> findById(int id) {
        String sql = "SELECT id, nombre, telefono, email FROM clientes WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Cliente> findByEmail(String email) {
        String sql = "SELECT id, nombre, telefono, email FROM clientes WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Cliente create(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, telefono, email) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación del cliente falló, ninguna fila afectada.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar cliente: " + e.getMessage(), e);
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }
}
