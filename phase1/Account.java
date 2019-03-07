package phase1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

abstract class Account {
    /*
    * There are two main types of accounts: Debt and Asset.
    */
    double balance;
    Login_Customer owner;
    Date dateOfCreation;
    private static final String inputFilePath = "/deposits.txt"; // not sure if this is the correct path
    static final String outputFilePath = "/outgoing.txt";
//    String mostRecentTransaction; // not sure if needed. e.g. "Withdraw: $20"

    /*
    What if we had a Transaction class that has all the undo methods?
    Plus it can have other methods share by Line of Credit and Asset accounts e.g. payBill, transfers
    These methods could be static...
     */

    public Account(double balance, Login_Customer owner) {
        this.balance = balance;
        this.owner = owner;

        Date date = new Date();
        this.dateOfCreation = date;
    }

    public Account(Login_Customer owner) {
        this(0, owner);
    }

    void deposit(double depositAmount) {
        // TODO what happen if depositAmount <= 0? Does the customer get a feedback?
        if (depositAmount > 0) {
            balance += depositAmount;
        }
    }

    void undoDeposit(double depositAmount) {
        balance -= depositAmount;
    }

    /*
    The above deposit method is more like a helper method.
    The real deposit method reads individual lines from an input file called <deposits.txt>
     */

    /**
     * Deposit money into their account by entering a cheque or cash into the machine
     * @throws IOException
     */
    void depositMoney() throws IOException {
        Path path = Paths.get(inputFilePath);
        try (BufferedReader fileInput = Files.newBufferedReader(path)) {
            String line = fileInput.readLine();
            while (line != null) { // Reading from a file will produce null at the end.
                double amountToDeposit = Double.valueOf(line.substring(1));
                deposit(amountToDeposit);
                line = fileInput.readLine();
            }
        }
    }

    /**
     * Withdraw money from an account (This will decrease <balance>)
     * TODO: notify the Cash class about this withdrawal
     * @param withdrawalAmount amount to be withdrawn
     * @return withdrawalAmount, otherwise 0.
     */
    abstract double withdraw(double withdrawalAmount);
    void undoWithdrawal(double withdrawalAmount) {
        balance += withdrawalAmount;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * A string representation of this account.
     * @return nice string rep.
     */
    @Override
    public String toString() {
        /*
        TODO: Include things like: most recent transaction, date of creation, account balance, username
         */
        return "";
    }

    public Login_Customer getOwner() {
        return owner;
    }
    //    abstract void undoMostRecentTransaction(); // TODO: figure out how to work with most recent transactions
}
