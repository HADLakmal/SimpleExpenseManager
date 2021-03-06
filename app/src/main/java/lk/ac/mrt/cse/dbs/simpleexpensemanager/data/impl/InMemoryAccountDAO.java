/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class InMemoryAccountDAO implements AccountDAO {
    private final DBHandler db;

    public InMemoryAccountDAO(Context activity) {
        db = new DBHandler(activity);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<Account> allAccount = db.getAllAccount();
        ArrayList res = new ArrayList();
        for (Account a: allAccount) {
            res.add(a.getAccountNo());
        }
        return res;
    }

    @Override
    public List<Account> getAccountsList() {
        return db.getAllAccount();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (db.hasAccountNo(accountNo)) {
            return db.getAccounts(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        Log.d("Logging", "addAccount Method");
        db.addAccount(account.getAccountNo(),
                account.getBankName(),
                account.getAccountHolderName(),
                account.getBalance());

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (db.hasAccountNo(accountNo)){
            db.removeAccount(accountNo);
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!db.hasAccountNo(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = db.getAccounts(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                db.updateBalance(accountNo, -1 * amount);
                break;
            case INCOME:
                db.updateBalance(accountNo, amount);
                break;
        }
    }
}
