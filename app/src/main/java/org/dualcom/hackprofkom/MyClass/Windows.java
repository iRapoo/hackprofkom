package org.dualcom.hackprofkom.MyClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Windows {
	public static void alert(Context context, String Title, String Text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(Title)
				.setMessage(Text)
				.setCancelable(true)
				.setPositiveButton("ОК",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
