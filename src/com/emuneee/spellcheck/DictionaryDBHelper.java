/**
 * Copyright 2012 emuneee apps
 * http://emuneee.com/apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emuneee.spellcheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author ehalley
 *
 */
public class DictionaryDBHelper extends SQLiteOpenHelper {
	private final static int VERSION = 1;
	private final static String DATABASE = "dictionary.db";
	public final static String WORDS_DB = "words";
	private final static String ID = "id";
	public final static String WORD = "word";
	private final static String WORDS_DB_QUERY = "CREATE TABLE " +
			WORDS_DB + " (" + ID + " integer primary key autoincrement, " +
			WORD + " text not null);";

			
	public DictionaryDBHelper(Context context) {
		super(context, DATABASE, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(WORDS_DB_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
