package transaction.server.account;

/*
 * Completed
 */
public class Account {

	 int id;
	 int balance;
	 
	public Account(int id, int startingBalance) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.balance = startingBalance;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}

	

}
