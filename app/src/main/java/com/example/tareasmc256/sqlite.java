package com.example.tareasmc256;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class sqlite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SystemTask";

    public static final String TABLANAMETAREAS = "Tareas";
    public static final String TABLANAMEUSERS = "Users";

    public sqlite(Context context){

        super(context,DATABASE_NAME,null,1);
    }

    public void  insertDataUsuarios(String name, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        String insert =  "INSERT INTO "+ TABLANAMEUSERS + "(id,nombre,image) VALUES(NULL,?,?)";
        SQLiteStatement statement = db.compileStatement(insert);
        statement.clearBindings();

        statement.bindString(1,name);
        statement.bindBlob(2,image);
        statement.executeInsert();
    }

    public void insertDataTareas(String titulo,int hora, int minuto , String descripcion, String fecha,String horas_designadas){
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "INSERT INTO " + TABLANAMETAREAS + "(id,titulo,hora, minuto,descripcion,fecha,horas_designadas)" +
                "VALUES(NULL,'" + titulo + "'," + hora + "," + minuto + ",'" + descripcion + "','" + fecha + "','" + horas_designadas+"')";

        SQLiteStatement statement = db.compileStatement(insert);
        statement.executeInsert();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLANAMEUSERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, image BLOG)";
        db.execSQL(CREATE_TABLE_USERS);
        String CREATE_TABLE_TAREAS = "CREATE TABLE " + TABLANAMETAREAS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT, " +
                "hora INTEGER, minuto INTEGER , descripcion TEXT, fecha TEXT, horas_designadas TEXT)";
        db.execSQL(CREATE_TABLE_TAREAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Tareas");
    }

    public void updateDataTareas(int id, String titulo,int hora, int minuto , String descripcion, String fecha,String horas_designadas){
        SQLiteDatabase db = this.getWritableDatabase();
        String update = "UPDATE " + TABLANAMETAREAS + " SET titulo = '" + titulo +"', hora = " +
                hora + ",minuto= " + minuto + ",descripcion = '" + descripcion + "', fecha = '" + fecha + "', horas_designadas = '" +
                horas_designadas +"' WHERE id = " + id ;
        SQLiteStatement statement = db.compileStatement(update);
        statement.execute();

    }

    public void updateDataUsers(String name ,byte[] image){

        SQLiteDatabase db = this.getWritableDatabase();
        String update = "UPDATE " + TABLANAMEUSERS + " SET nombre = ? , image = ? WHERE id = 1";
        SQLiteStatement statement = db.compileStatement(update);
        statement.clearBindings();
        statement.bindString(1,name);
        statement.bindBlob(2,image);
        statement.executeInsert();
    }

    public void deleteTareas(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM Tareas WHERE id =" + id ;
        SQLiteStatement statement = db.compileStatement(delete);
        statement.execute();
    }

    public Cursor querySpecialTareas(String tituloTarea){
        String query = "SELECT * FROM " + TABLANAMETAREAS + " WHERE titulo = '" + tituloTarea + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor queryAllRegistersUsers(){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM " +  TABLANAMEUSERS , null);
    }

    public Cursor queryAllRegistersTareas(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLANAMETAREAS, null );
    }

}
