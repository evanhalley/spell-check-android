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

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Window;

/**
 * Houses the Spell Check app.  Contains two fragments
 * @author ehalley
 *
 */
public class MainActivity extends Activity {
	private static DictionaryDBHelper mHelper;
	private static Map<String, String> mWordMap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminate(true);
        setProgressBarVisibility(false);
        mHelper = new DictionaryDBHelper(this);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab spellCheckTab = actionBar.newTab();
        spellCheckTab.setText(R.string.check_spelling);
        spellCheckTab.setTabListener(new TabListener<SpellCheckFragment>(
        		this, "spellcheck", SpellCheckFragment.class));
        
        Tab addWordTab = actionBar.newTab();
        addWordTab.setText(R.string.add_word);
        addWordTab.setTabListener(new TabListener<AddWordFragment>(
        		this, "addword", AddWordFragment.class));
        
        actionBar.addTab(spellCheckTab, true);
        actionBar.addTab(addWordTab);
        
        new InitializeWordMap().execute();
    }
    
    /**
     * Returns the DictionaryDBHelper
     * @return
     */
    public static DictionaryDBHelper getDictionaryDBHelper() {
    	return mHelper;
    }
    
    /**
     * returns the word map
     * @return
     */
    public static Map<String, String> getWordMap() {
    	return mWordMap;
    }

    /**
     * Handles interaction with the tabs present under the action bar
     * @author ehalley
     *
     * @param <T>
     */
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
          * @param activity  The host Activity, used to instantiate the fragment
          * @param tag  The identifier tag for the fragment
          * @param clz  The fragment's Class, used to instantiate the fragment
          */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }
    
    /**
     * Initializes the word map with values from the database
     */
    public static void initializeWordMap() {
		DictionaryDBHelper helper = MainActivity.getDictionaryDBHelper();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT * FROM " + DictionaryDBHelper.WORDS_DB, null);
		if(cursor != null) {
			mWordMap = new HashMap<String, String>(cursor.getCount());
			while(cursor.moveToNext()) {
				String word = cursor.getString(
						cursor.getColumnIndex(DictionaryDBHelper.WORD));
				mWordMap.put(word, word);
			}
			cursor.close();
		}
	}
	
    /**
     * AsyncTask that runs the method to the initialize the word map
     * @author ehalley
     *
     */
	public static class InitializeWordMap extends AsyncTask<Void, Void, Void> {
			
		@Override
		protected Void doInBackground(Void... arg0) {
			initializeWordMap();
			return null;
		}
	}
}
