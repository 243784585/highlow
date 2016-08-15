package com.omdd.gdyb.utils;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.omdd.gdyb.bean.FlashAirFile;

public class FileDao extends SQLiteOpenHelper {

	public FileDao(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public FileDao(Context context, int version){
		super(context, "flashair.db", null, version);
	}
	//_id,planNo,fileName,localPath,flashAirPath,size,state,fileTime,createTime
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table filelist(_id integer primary key autoincrement,planNo char(30),fileName vachar(300),localPath varchar(300),flashAirPath varchar(300),size integer,state integer,fileTime datetime,createTime datetime default current_timestamp)");
//		db.execSQL("create table projectInfo(_id integer primary key autoincrement,)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}
	
	public void saveFlashAirFile(FlashAirFile file){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("planNo",file.planNo);
		cv.put("fileName",file.fileName);
		cv.put("localPath",file.localPath);
		cv.put("flashAirPath",file.flashAirPath);
		cv.put("size",file.size);
		cv.put("state",file.state);
		cv.put("fileTime",file.fileTime);
		db.insert("filelist",null,cv);
//		db.execSQL("insert into filelist(planNo,fileName,localPath,flashAirPath,size,state,fileTime) values(?,?,?,?,?,?,?)",
//				new Object[]{file.planNo,file.fileName,file.localPath,file.flashAirPath,file.size,file.state,file.fileTime});
		db.close();
	}
	
	public List<FlashAirFile> queryFlashAirFileByUnDownload(List<FlashAirFile> files,String planNo){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from filelist where planNo = ? and (state = ? or state = ? or state = ?)", new String[]{planNo,String.valueOf(FlashAirFile.STATE_UNLOAD),String.valueOf(FlashAirFile.STATE_LOADFAILED),String.valueOf(FlashAirFile.STATE_UPFAILED)});
		
		if(files == null)
			files = new ArrayList<FlashAirFile>();
		else
			files.clear();
		
		while(cursor.moveToNext()){
			files.add(
					new FlashAirFile(
							cursor.getInt(cursor.getColumnIndex("_id")),//_id
							cursor.getString(cursor.getColumnIndex("planNo")),//planNo
							cursor.getString(cursor.getColumnIndex("localPath")),//localPath
							cursor.getString(cursor.getColumnIndex("flashAirPath")),//flashAirPath
							cursor.getString(cursor.getColumnIndex("fileName")),//fileName
							cursor.getInt(cursor.getColumnIndex("size")),//size,
							cursor.getLong(cursor.getColumnIndex("fileTime")),//fileTime
							cursor.getInt(cursor.getColumnIndex("state"))//state
					)
			);
		}
		cursor.close();
		db.close();
		return files.isEmpty() ? null:files;
	}

	/**
	 * 查找工程号对应的文件
	 * @param files
	 * @param planNo
     * @return
     */
	public List<FlashAirFile> queryFlashAirFileByPlanNo(List<FlashAirFile> files,int planNo){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from filelist where planNo = ?", new String[]{String.valueOf(planNo)});

		if(files == null)
			files = new ArrayList<FlashAirFile>();
		else
			files.clear();

		while(cursor.moveToNext()){
			files.add(
					new FlashAirFile(
							cursor.getInt(cursor.getColumnIndex("_id")),//_id
							cursor.getString(cursor.getColumnIndex("planNo")),//planNo
							cursor.getString(cursor.getColumnIndex("localPath")),//localPath
							cursor.getString(cursor.getColumnIndex("flashAirPath")),//flashAirPath
							cursor.getString(cursor.getColumnIndex("fileName")),//fileName
							cursor.getInt(cursor.getColumnIndex("size")),//size,
							cursor.getLong(cursor.getColumnIndex("fileTime")),//fileTime
							cursor.getInt(cursor.getColumnIndex("state"))//state
					)
			);
		}
		cursor.close();
		db.close();
		return files.isEmpty() ? null:files;
	}

	public List<FlashAirFile> queryAll(){
		List<FlashAirFile> infos = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from filelist",null);
		while(cursor.moveToNext()){
					infos.add(new FlashAirFile(
							cursor.getInt(cursor.getColumnIndex("_id")),//_id
							cursor.getString(cursor.getColumnIndex("planNo")),//planNo
							cursor.getString(cursor.getColumnIndex("localPath")),//localPath
							cursor.getString(cursor.getColumnIndex("flashAirPath")),//flashAirPath
							cursor.getString(cursor.getColumnIndex("fileName")),//fileName
							cursor.getInt(cursor.getColumnIndex("size")),//size,
							cursor.getLong(cursor.getColumnIndex("fileTime")),//fileTime
							cursor.getInt(cursor.getColumnIndex("state"))//state
					));
		}
		cursor.close();
		db.close();
		return infos;
	}


	
	public void updateFlashAirFile(FlashAirFile file){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("state",file.state);
		cv.put("fileTime",file.fileTime);
		db.update("filelist", cv, "_id = ? ", new String[]{String.valueOf(file._id)});
		db.close();
//		db.execSQL("update note set title=?,content=? where _id=?",new String[]{note.title,note.content,note._id});
	}

	/**
	 * 通过planNo,flashAirPath,fileName,fileTime查找一条记录
	 * @param file
	 * @return 有记录返回传入对象,无记录返回null
     */
	public FlashAirFile queryFlashAirFileById(FlashAirFile file){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("filelist",null,"planNo = ? and flashAirPath = ? and fileName = ? ",new String[]{String.valueOf(file.planNo),file.flashAirPath,file.fileName},null,null,null);
		if(cursor.moveToNext()){
			long fileTime = cursor.getLong(cursor.getColumnIndex("fileTime"));
			if(fileTime < file.fileTime){
				file.state = FlashAirFile.STATE_UNLOAD;
				file._id = cursor.getInt(cursor.getColumnIndex("_id"));
			}
		}else{
			file = null;
		}
		cursor.close();
		db.close();
		return file;
	}

	/**
	 * 清空表中数据
	 */
	public void clearData(){
		SQLiteDatabase db = getWritableDatabase();
		db.delete("filelist",null,null);
	}

}
