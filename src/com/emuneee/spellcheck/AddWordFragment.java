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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Shows a list of words in the dictionary.  Also
 * allows the adding of words
 * @author ehalley
 *
 */
public class AddWordFragment extends Fragment {
	public final static String TAG = "AddWordFragment";
	private ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_word_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listViewWords);
		Map<String, String> wordMap = MainActivity.getWordMap();
		List<String> words = new ArrayList<String>(wordMap.values());
		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, words));
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_word_fragment, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_add_word:
				showAddWordDialog();
				break;
		}
		return true;
	}

	/**
	 * Brings up the dialog to show the interface to add a word
	 * to the database
	 */
	private void showAddWordDialog() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
				Activity.LAYOUT_INFLATER_SERVICE);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = inflater.inflate(R.layout.add_word, null);
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		final EditText editText = (EditText) view.findViewById(R.id.editTextAddWord);
		((Button) view.findViewById(R.id.buttonAddWord)).setOnClickListener(
				new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String wordToAdd = editText.getText().toString();
				new AddWordTask().execute(wordToAdd);
			}
		});
		dialog.show();
	}
	
	/**
	 * Adds a new word to the database
	 * @author ehalley
	 *
	 */
	private class AddWordTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... arg0) {
			SQLiteDatabase database = MainActivity.getDictionaryDBHelper()
					.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DictionaryDBHelper.WORD, arg0[0]);
			try {
				database.beginTransaction();
				database.insert(DictionaryDBHelper.WORDS_DB, null, values);
				database.setTransactionSuccessful();
				MainActivity.initializeWordMap();
			} catch (SQLException e) {
				Log.w(TAG, "Error inserting word int the database");
				return false;
			} finally {
				database.endTransaction();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result) {
				Toast.makeText(getActivity(), R.string.word_added, Toast.LENGTH_SHORT).show();
				Map<String, String> wordMap = MainActivity.getWordMap();
				List<String> words = new ArrayList<String>(wordMap.values());
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
						android.R.layout.simple_list_item_1, words);
				mListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity(), R.string.word_not_add, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
