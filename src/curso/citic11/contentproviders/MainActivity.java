package curso.citic11.contentproviders;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import curso.citic11.contentproviders.ClientsProvider.Clients;

public class MainActivity extends Activity {
	
	private Button btnInsert;
	private Button btnRead;
	private Button btnDelete;
	private Button btnCall;
	private EditText text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		text = (EditText)findViewById(R.id.edtTxt);
		btnInsert = (Button)findViewById(R.id.btnInsert);
		btnRead = (Button)findViewById(R.id.btnRead);
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnCall = (Button)findViewById(R.id.btnCall);
		
		btnRead.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				readClients();
			}
		});

		btnInsert.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				insertClient();
			}
		});
		
		btnDelete.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				deleteClient();
			}
		});		
		
		btnCall.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				calls();
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void readClients() {
		// Columnas de la tabla a recuperar
		String[] projection = new String[] { 
				Clients._ID,
				Clients.C_NAME, 
				Clients.C_PHONE,
				Clients.C_EMAIL 
		};

		Uri clientesUri = ClientsProvider.CONTENT_URI;
		ContentResolver cr = getContentResolver();

		// Hacemos la consulta
		Cursor cur = cr.query(clientesUri, projection, 
				null, 
				null, 
				null); 

		try {
			if (cur.moveToFirst()) {
				String name;
				String phone;
				String email;

				int cname = cur.getColumnIndex(Clients.C_NAME);
				int cphone = cur.getColumnIndex(Clients.C_PHONE);
				int cemail = cur.getColumnIndex(Clients.C_EMAIL);

				text.setText("");

				do {
					name = cur.getString(cname);
					phone = cur.getString(cphone);
					email = cur.getString(cemail);

					text.append(name + " - " + phone + " - " + email + "\n");

				} while (cur.moveToNext());
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Ha habido un error...", Toast.LENGTH_LONG).show();
		}		
	}
	
	private void insertClient() {
		ContentValues values = new ContentValues();

		values.put(Clients.C_NAME, "ClienteN");
		values.put(Clients.C_PHONE, "999111222");
		values.put(Clients.C_EMAIL, "nuevo@email.com");

		ContentResolver cr = getContentResolver();

		cr.insert(ClientsProvider.CONTENT_URI, values);		
	}
	
	private void deleteClient() {
		ContentResolver cr = getContentResolver();
		cr.delete(ClientsProvider.CONTENT_URI, Clients.C_NAME + " = 'ClienteN'", null);		
	}

	private void calls() {
		String[] projection = new String[] { Calls.TYPE, Calls.NUMBER };

		Uri llamadasUri = Calls.CONTENT_URI;

		ContentResolver cr = getContentResolver();

		Cursor cur = cr.query(llamadasUri, projection, // Columnas a
														// devolver
				null, // Condici�n de la query
				null, // Argumentos variables de la query
				null); // Orden de los resultados

		if (cur.moveToFirst()) {
			int type;
			String callType = "";
			String phone;

			int ctype = cur.getColumnIndex(Calls.TYPE);
			int cnumber = cur.getColumnIndex(Calls.NUMBER);
			
			
			text.setText("");

			do {

				type = cur.getInt(ctype);
				phone = cur.getString(cnumber);

				if (type == Calls.INCOMING_TYPE)
					callType = "ENTRANTE";
				else if (type == Calls.OUTGOING_TYPE)
					callType = "SALIENTE";
				else if (type == Calls.MISSED_TYPE)
					callType = "PERDIDA";

				text.append(callType + " - " + phone + "\n");

			} while (cur.moveToNext());
		} else {
			text.setText("No hay llamadas");
		}
	}
}
