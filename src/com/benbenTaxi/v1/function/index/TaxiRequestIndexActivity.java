package com.benbenTaxi.v1.function.index;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.BenbenApplication;
import com.benbenTaxi.v1.function.ShowDetail;
import com.benbenTaxi.v1.function.actionbar.ActionBarActivity;
import com.benbenTaxi.v1.function.taxirequest.TaxiRequest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;  
import android.view.ViewGroup;
import android.widget.BaseAdapter;  
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView; 
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

public class TaxiRequestIndexActivity extends ActionBarActivity  {
	
	public final static int MSG_HANDLE_INDEX_TASK_START		    =	0;
	public final static int MSG_HANDLE_INDEX_TASK_SUCCESS		=	1;
	public final static int MSG_HANDLE_INDEX_TASK_ERROR			= 	2;
	
	//private final String TAG			     					= TaxiRequestDetail.class.getName();
	private MyAdapter adapter				 					= null;  
    private ListView mListView				 					= null;  
    private BenbenApplication mApp			 					= null;
    private ProgressBar mProgress			 					= null;
    private TaxiRequestIndexTask  mTaxiRequestIndexTask 		= null;
    
    
    Handler MsgHandler = new Handler() {
        public void handleMessage(android.os.Message msg) 
        {
        	switch (msg.what) {
        		case MSG_HANDLE_INDEX_TASK_START:
        			mProgress.setVisibility(View.VISIBLE);
        			break;
        		case MSG_HANDLE_INDEX_TASK_SUCCESS:
        			mProgress.setVisibility(View.GONE);
        			showList((TaxiRequestIndexResponse) msg.obj);
        			break;
        		case MSG_HANDLE_INDEX_TASK_ERROR:
        			String errMsg = (String) msg.obj;
        			Toast.makeText(TaxiRequestIndexActivity.this,errMsg, Toast.LENGTH_LONG).show();
        			break;
        		default:
        			break;
        	}
        }
    };
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_taxirequstindex);
		mApp						= (BenbenApplication)this.getApplication();
		mListView 					= (ListView)findViewById(R.id.lv);  
		mProgress					= (ProgressBar)findViewById(R.id.taxi_request_index_progressbar);
		mTaxiRequestIndexTask 		= new TaxiRequestIndexTask(this,mApp,MsgHandler);
		mTaxiRequestIndexTask.go();

		mListView.setOnItemClickListener(new OnItemClickListener(){			
			@Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
				    MyAdapter 		myAdapter 				= (MyAdapter) arg0.getAdapter();
                	ShowDetail.showCurrentPassengerRequest((TaxiRequest) myAdapter.getItem(arg2), 
                			TaxiRequestIndexActivity.this, mApp);               
            }     
        });		
		
    }
    
    protected void showList(TaxiRequestIndexResponse taxiRequestIndexResponse){    	
    	mListView = (ListView)findViewById(R.id.lv);  
    	if(taxiRequestIndexResponse==null)
    		return;
		adapter = new MyAdapter(this,taxiRequestIndexResponse); 
		mListView.setAdapter(adapter);  
    }
    
    protected class MyAdapter extends BaseAdapter {        
    	private LayoutInflater   		mInflater;        
    	TaxiRequestIndexResponse 		mTaxiRequestIndexResponse;
    	public MyAdapter(Context context,TaxiRequestIndexResponse taxiRequestIndexResponse)
    	{           
    		mInflater 					= LayoutInflater.from(context); 
    		mTaxiRequestIndexResponse	= taxiRequestIndexResponse;
    	}    
    	
    	@Override        
    	public int getCount() {
    		if (mTaxiRequestIndexResponse != null)
    			return mTaxiRequestIndexResponse.getSize();
    		return 0;
    	}            
    	@Override       
    	public Object getItem(int position) { 
    		if (mTaxiRequestIndexResponse != null && mTaxiRequestIndexResponse.getSize() > position)
    			return  mTaxiRequestIndexResponse.getTaxiRequest(position);
    		return null;
    	}            
    	@Override        
    	public long getItemId(int position) {
    		return position;        
    		}            
    	@Override        
    	public View getView(int position, View convertView, ViewGroup parent) {
    		ViewHolder holder = null;           
    		if (convertView == null) { 
    			holder = new ViewHolder();  
    			convertView = mInflater.inflate(R.layout.taxi_requestindex_item, null); 
    			holder.date 	= (TextView) convertView.findViewById(R.id.date);
    			holder.month	= (TextView) convertView.findViewById(R.id.taxi_request_index_month);
    			holder.source = (TextView) convertView.findViewById(R.id.source);    
    			holder.state = (TextView) convertView.findViewById(R.id.state); 
    			convertView.setTag(holder);            
    		} else {   
    			holder = (ViewHolder) convertView.getTag(); 
    		}         		
    		
    		TaxiRequest tx=(TaxiRequest) mTaxiRequestIndexResponse.getTaxiRequest(position);
    			
    		holder.date.setText(tx.getCreatedAt("dd日"));
    		holder.month.setText(tx.getCreatedAt("MM月"));
    		holder.source.setText("打车位置:"+tx.getSource());
    		holder.state.setText("交易状态:"+tx.getHumanBreifTextState());
    		if(tx.isTaxiRequestSuccess())
    			holder.state.setTextColor(Color.GREEN);
    		else
    			holder.state.setTextColor(Color.RED);
//    		holder.state.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
    		//holder.cBox.setChecked(isSelected.get(position));            
    		return convertView;        
    	}            
    	
    	public final class ViewHolder { 
    		
    		public TextView date;
    		public TextView month;
    		public TextView source;  
    		public TextView state;   		   
    	}       	
    }
    
}



