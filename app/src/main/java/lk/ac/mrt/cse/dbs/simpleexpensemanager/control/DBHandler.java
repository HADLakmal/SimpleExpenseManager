package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {
        super(context, "140334N.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create TABLE accounts (" +
                "ac_no TEXT PRIMARY KEY, " +
                "bank_name TEXT NOT NULL," +
                "holder_name TEXT NOT NULL," +
                "balance DOUBLE DEFAULT 0);");
        sqLiteDatabase.execSQL("create TABLE transactions (" +
                "ac_no TEXT," +
                "date TEXT," +
                "expense_type boolean," +
                "amount DOUBLE," +
                "FOREIGN KEY(account_no) REFERENCES accounts(account_no) );");

        Log.d("Logging", "Create two Tables on onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS accounts;");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transactions;");
        onCreate(sqLiteDatabase);
        Log.d("Logging", "Delete Two Table on onUpgrade");
    }

    // Account

    public boolean addAccount(String no, String bankName, String HolderName, double balance){
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", balance);
        contentValues.put("ac_no", no);
        contentValues.put("bank_name", bankName);
        contentValues.put("holder_name", HolderName);


        if (!hasAccountNo(no)) {
            Log.d("Logging", "Account dosent exist");
            try {
                this.getWritableDatabase().insert("accounts", "", contentValues);
                Log.d("Logging", "Added correctly in addAccount -> " + no);
                return true;
            } catch (SQLException e) {
                Log.d("Logging", "in addAccount -> " + no + " -- " + e.toString());
                return false;
            }
        } else {
            Log.d("Logging", "Account is exist");
            return false;
        }
    }

    public void removeAccount(String accNo){
        if (hasAccountNo(accNo)) {
            this.getWritableDatabase().delete("accounts", "ac_no = ?", new String[]{accNo});
            Log.d("Logging", "delete Accounts " + accNo);
        }else{
            Log.d("Logging", "delete Accounts Fail" + accNo);
        }
    }

    public List<Account> getAllAccount(){
        Cursor cur = this.getReadableDatabase().rawQuery("SELECT * FROM accounts", null);
        List<Account> all = new ArrayList<>();

        while (cur.moveToNext()){
            all.add(new Account(cur.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getDouble(3)));
        }

        Log.d("Logging", "get all Accounts");
        return all;
    }


    public void updateAccount(String no, String bankName, String accountHolderName, double balence){
        ContentValues contentValues = new ContentValues();
        contentValues.put("account_no", no);
        contentValues.put("bank_name", bankName);
        contentValues.put("account_holder_name", accountHolderName);
        contentValues.put("balance", balence);

        if (checkNo(no)) {
            try {
                this.getWritableDatabase().update("accounts", contentValues,
                        "account_no = ?", new String[]{no});
                Log.d("Logging", "UpdateAccount done ");
            } catch (SQLException e) {
                Log.d("Logging", "UpdateAccount error-> " + e.toString());
                e.printStackTrace();
            }
        }else{
            Log.d("Logging", "has no account to update");
        }
    }

    public Account getAccounts(String accNo){
        Cursor cur = this.getReadableDatabase().query("accounts", null, "ac_no = ?", new String[]{accNo}, null, null, null);
        if (cursor.moveToNext()){
            return new Account(curs.getString(0),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getDouble(3));
        }
        Log.d("Logging", "get Accounts " + accNo);
        return null;
    }

    public void updateBalance(String no, double amount){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT balance FROM accounts where account_no = ?",
                new String[]{no});
        Log.d("Logging", "Start Update Balance" + no + " " + amount);
        if (cursor.moveToNext()){
            double blce = cursor.getDouble(0);
            blce += amount;
            try {
                this.getWritableDatabase().execSQL("UPDATE accounts SET balance = ? WHERE account_no=?"
                    , new String[]{blce + "", no});
                Log.d("Logging", "Updated " + no);
            } catch (SQLException e) {
                Log.d("Logging", "UpdateBalance " + no + " " + e.toString());
            }
        }
    }


    //Transaction
    public void addTransaction(String accNo, Date date, ExpenseType expenseType, double amount){
        Log.d("Logging", "addTransaction start " + accNo);
        ContentValues contentValues = new ContentValues();
        contentValues.put("ac_no", accNo);
        contentValues.put("date", (new SimpleDateFormat("yyyy-MM-dd")).format(date));
        contentValues.put("expense_type", expenseType == ExpenseType.EXPENSE);
        contentValues.put("amount", amount);

        try {
            this.getWritableDatabase().insertOrThrow("transactions", "", contentValues);
        } catch (SQLException e) {
            Log.d("Logging", "addTransaction Error " + accNo + "  " + e.toString());
        }
    }

    public List<Transaction> getAllTransactions(){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM transactions", null);
        List<Transaction> all = new ArrayList<>();

        while (cursor.moveToNext()){
            try {
                all.add(new Transaction((new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1))) ,
                        cursor.getString(0),
                        (cursor.getInt(2) > 0 ? ExpenseType.EXPENSE : ExpenseType.INCOME),
                        cursor.getDouble(3)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return all;
    }
    public boolean checkNo(String accNo){
        Log.d("Logging", "checkNo " + accNo);
        if (this.getReadableDatabase().query("accounts",
                null, "account_no = ?", new String[]{accNo},null,null,null)
                .moveToNext()){
            return true;
        }
        return false;
    }

}
