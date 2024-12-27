package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    //create account
    public Account registerAccount(Account account) {
        Account createAccount = accountDAO.insertAccount(account);
        return createAccount;
    }

    //retrieve login
    public Account getLogin(String username, String password) {
        return accountDAO.getLogin(username, password);
    }

    //get all accounts by id
    public Account getAccountById(int accountId) {
        return accountDAO.getAccountById(accountId);
    }
}