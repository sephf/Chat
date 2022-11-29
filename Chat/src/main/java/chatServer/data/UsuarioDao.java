package chatServer.data;

import chatProtocol.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    Database db;
    public UsuarioDao(){
        db= Database.instance();
    }
    public void create(User e)throws Exception{
        String sql="insert into Usuario (id,clave,nombre) values(?,?,?)";
        PreparedStatement stm=db.prepareStatement(sql);
        stm.setString(1, e.getId());
        stm.setString(2, e.getClave());
        stm.setString(3, e.getNombre());
        db.executeUpdate(stm);
    }
    public User from(ResultSet resultSet,String alias) throws SQLException {
        User u= new User("","","");
        u.setId(resultSet.getString(alias+".id"));
        u.setClave(resultSet.getString(alias+".clave"));
        u.setNombre(resultSet.getString(alias+".nombre"));
        return  u;
    }
    public User read(String id)throws Exception{
        String sql="select * from Usuario u where u.id=?";
        PreparedStatement stm=db.prepareStatement(sql);
        stm.setString(1,id);
        ResultSet resultSet=db.executeQuery(stm);
        if(resultSet.next()){
            return from(resultSet,"u");
        }else throw new Exception("USUARIO NO EXISTE");
    }
    public void update(User u)throws Exception{
        String sql="update Usuario set clave=?,nombre=? where id=?";
        PreparedStatement stm= db.prepareStatement(sql);
        stm.setString(1,u.getClave());
        stm.setString(2,u.getNombre());
        stm.setString(3,u.getId());
        if(db.executeUpdate(stm)==0){
            throw new Exception("USUARIO NO EXISTE");
        }
    }
    public void delete(User u)throws Exception{
        String sql="delete from Usuario where id=?";
        PreparedStatement stm=db.prepareStatement(sql);
        stm.setString(1, u.getId());
        if(db.executeUpdate(stm)==0){
            throw new Exception("USUARIO NO EXISTE");
        }
    }
    public List<User> findByName(String nombre) throws Exception {
        List<User> resultado = new ArrayList<User>();
        String sql = "select * " +
                "from " +
                "Usuario u " +
                "where u.nombre like ?";
        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, "%" + nombre + "%");
        ResultSet rs = db.executeQuery(stm);
        while (rs.next()) {
            resultado.add(from(rs, "u"));
        }
        return resultado;
    }

}
