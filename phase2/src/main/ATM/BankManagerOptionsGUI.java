package ATM;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * A GUI for Bank Manager.
 */
public class BankManagerOptionsGUI extends EmployeeOptionsGUI {

    BankManagerOptionsGUI(Stage mainWindow, Scene welcomeScreen, User user) {
        super(mainWindow, welcomeScreen, user);
    }

    @Override
    public Scene createOptionsScreen() {
        addOptionText("Read alerts");
        addOptionText("Create user");
        addOptionText("Create bank account for user");
        addOptionText("Create joint account");
        addOptionText("Restock ATM");
        addOptionText("Undo transactions");
        addOptionText("Change password");
        addOptionText("Clear all bank data");
        addOptionText("Set youth max transactions");
        addOptionText("Set youth max transfers");
        addOptionText("Manage GIC");
        addOptionText("Logout");

        addOptions();

        getOption(0).setOnAction(event -> readAlertsScreen());
        getOption(1).setOnAction(event -> createUserScreen());
        getOption(2).setOnAction(event -> createBankAccountScreen());
        getOption(3).setOnAction(event -> createJointAccountScreen());
        getOption(4).setOnAction(event -> restockATMScreen());
        getOption(5).setOnAction(event -> undoTransactionsScreen());
        getOption(7).setOnAction(event -> clearBankDataScreen());
        getOption(8).setOnAction(event -> setYouthTransactionsScreen());
        getOption(9).setOnAction(event -> setYouthTransfersScreen());
        getOption(10).setOnAction(event -> ManageGICScreen());

        return generateOptionsScreen();
    }

    private void createUserScreen() {
        GridPane gridPane = createFormPane();

        Label userType = new Label("User Type:");
        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        for (String type : ATM.userManager.USER_TYPE_NAMES) {
            choiceBox.getItems().add(type);
        }

        Label usernameLbl = new Label("Username:");
        Label passwordLbl = new Label("Password:");

        TextField usernameInput = new TextField();
        PasswordField passwordField = new PasswordField();

        Button cancel = new Button("Cancel");
        Button create = new Button("Create");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(create);

        gridPane.add(userType, 0, 0);
        gridPane.add(choiceBox, 1, 0);
        gridPane.add(usernameLbl, 0, 1);
        gridPane.add(usernameInput, 1, 1);
        gridPane.add(passwordLbl, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(hbBtn, 1, 3);

        // I'm not gonna check the validity of password

        cancel.setOnAction(event -> window.setScene(optionsScreen));
        create.setOnAction(event -> {
            String typeSimpleName = choiceBox.getValue();
            String username = usernameInput.getText();
            String password = passwordField.getText();
            boolean created = ATM.userManager.createAccount(typeSimpleName, username, password);

            if (created && typeSimpleName.equals(Customer.class.getSimpleName())) {
                createDOBScreen(username);
            } else {
                showAlert(Alert.AlertType.ERROR, window, "Error", "We are sorry user couldn't be created");
                window.setScene(optionsScreen);
            }
        });

        window.setScene(new Scene(gridPane));
    }

    private void createDOBScreen(String username) {
        GridPane gridPane = createFormPane();

        Label question = new Label("Would you like to set the\nDate of Birth as well?");
        Button yes = new Button("Yes");
        Button no = new Button("No");
        HBox hbBtn = new HBox(10);
        hbBtn.getChildren().add(yes);
        hbBtn.getChildren().add(no);

        Button submit = new Button("Submit");

        gridPane.add(question, 0, 0);
        gridPane.add(hbBtn, 1, 0);

        final Text actionTarget = new Text();
        TextField dobInput = new TextField();
        gridPane.add(actionTarget, 0, 3);

        no.setOnAction(event -> window.setScene(optionsScreen));
        yes.setOnAction(event -> {
            gridPane.getChildren().remove(question);
            gridPane.getChildren().remove(hbBtn);
            actionTarget.setFill(Color.BLACK);
            actionTarget.setText("Enter Date of Birth (YYYY-MM-DD):");
            gridPane.add(dobInput, 1, 3);
            gridPane.add(submit, 1, 4);

            submit.setOnAction(event1 -> {
                String _dob = dobInput.getText();
                System.out.println(_dob);
                System.out.println(username);
                try {
                    String dob = LocalDate.parse(_dob).toString();
                    ((Customer) ATM.userManager.getUser(username)).setDob(dob);
                    showAlert(Alert.AlertType.CONFIRMATION, window, "Success", "Date of Birth for " + username + " is set to " + dob);
                } catch (DateTimeException e) {
                    showAlert(Alert.AlertType.ERROR, window, "Error", "Are you sure you born on a day that doesn't exist?");
                } finally {
                    window.setScene(optionsScreen);
                }
            });
        });

        window.setScene(new Scene(gridPane, 400, 200));

    }

    private void restockATMScreen() {
        GridPane grid = createFormPane();

        Button cancel = new Button("Cancel");
        Button restock_ = new Button("Restock");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(restock_);

        int rowIndex = 0;

        HashMap<Integer, TextField> textField = new HashMap<>();

        for (Integer d : ATM.banknoteManager.DENOMINATIONS) {
            // Label
            Label dLabel = new Label("Enter amount of $" + d + " dollar bill: ");
            grid.add(dLabel, 0, rowIndex);

            // TextField
            TextField dTextField = new TextField();
            grid.add(dTextField, 1, rowIndex);
            textField.put(d, dTextField);

            rowIndex++;
        }

        grid.add(hbBtn, 1, 4);

        cancel.setOnAction(event -> window.setScene(optionsScreen));
        restock_.setOnAction(event -> {
            Map<Integer, Integer> restock = new HashMap<>();

            for (Integer d : textField.keySet()) {
                restock.put(d, Integer.valueOf(textField.get(d).getText()));
            }

            ((BankManager) user).restockMachine(restock);
            showAlert(Alert.AlertType.CONFIRMATION, window, "Success", "Restocking success! The current stock is " + ATM.banknoteManager.banknotes);
            window.setScene(optionsScreen);
        });

        window.setScene(new Scene(grid));
    }

    private void clearBankDataScreen() {
        GridPane grid = createFormPane();

        Label warningLbl = new Label("WARNING: Committing a fraud with value exceeding one million dollars\nmight result in 14 year jail sentence!");
        Button proceed = new Button("Proceed");
        Button cancel = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(proceed);

        grid.add(warningLbl, 0, 0);
        grid.add(hbBtn, 1, 1);

        cancel.setOnAction(event -> window.setScene(optionsScreen));
        proceed.setOnAction(event -> {
            ATM.serialization.deleteDatabase();
            showAlert(Alert.AlertType.CONFIRMATION, window, "Cleared", "Data has been cleared. Good Luck!");
            window.close();
            System.exit(0);
        });

        window.setScene(new Scene(grid));
    }

    private void setYouthTransactionsScreen() {
        GridPane grid = createFormPane();

        Label youthLabel = new Label("Choose User with Youth Account:");
        TextField youthUser = new TextField();
        Label transactionLabel = new Label("Choose Number of Max Transactions:");
        TextField transactions = new TextField();
        Button set = new Button("Set");
        Button cancel = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(set);

        grid.add(youthLabel, 0, 0);
        grid.add(youthUser, 1, 0);
        grid.add(transactionLabel, 0, 1);
        grid.add(transactions, 1, 1);
        grid.add(hbBtn, 1, 2);

        cancel.setOnAction(event -> window.setScene(optionsScreen));
        set.setOnAction(event -> {
            int amount = Integer.valueOf(transactions.getText());
            String youthAccount = youthUser.getText();
            if (ATM.userManager.isPresent(youthAccount)) {
                List<Account> AccountIDList = ATM.accountManager.getListOfAccounts(youthAccount);
                for (Account accounts: AccountIDList) {
                    if (accounts instanceof Youth) {
                        ((BankManager) this.user).setMaxTransactions((Youth) accounts, amount);
                        showAlert(Alert.AlertType.CONFIRMATION, window, "Error", "Transaction Limit Set");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, window, "Error", "Transaction Limit Failed: No Youth Account Found");
            }
            window.setScene(optionsScreen);
        });

        window.setScene(new Scene(grid));
    }


    private void setYouthTransfersScreen() {
        GridPane grid = createFormPane();

        Label youthLabel = new Label("Choose User with Youth Account:");
        TextField youthUser = new TextField();
        Label transactionLabel = new Label("Choose Transfer Limit:");
        TextField transactions = new TextField();
        Button set = new Button("Set");
        Button cancel = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(set);

        grid.add(youthLabel, 0, 0);
        grid.add(youthUser, 1, 0);
        grid.add(transactionLabel, 0, 1);
        grid.add(transactions, 1, 1);
        grid.add(hbBtn, 1, 2);

        cancel.setOnAction(event -> window.setScene(optionsScreen));
        set.setOnAction(event -> {
            int amount = Integer.valueOf(transactions.getText());
            String youthAccount = youthUser.getText();
            if (ATM.userManager.isPresent(youthAccount)) {
                List<Account> AccountIDList = ATM.accountManager.getListOfAccounts(youthAccount);
                for (Account accounts: AccountIDList) {
                    if (accounts instanceof Youth) {
                        ((BankManager) this.user).setTransferLimit((Youth) accounts, amount);
                        showAlert(Alert.AlertType.CONFIRMATION, window, "Success", "Transfer Limit has been set");
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, window, "Error", "Transfer Limit failed: No Youth Account Found");
            }
            window.setScene(optionsScreen);
        });
        window.setScene(new Scene(grid));
    }

    private void ManageGICScreen() {
        GridPane grid = createFormPane();

        Button remove = new Button("Remove GIC");
        Button view = new Button("view GIC");
        Button create = new Button("Create GIC");
        Button cancel = new Button("Cancel");
        Label indexLabel = new Label("id of GIC");
        TextField index = new TextField();
        Label periodLabel = new Label("Period In Months");
        TextField period = new TextField();
        Label rateLabel = new Label("Rate after the Period in Percentage");
        TextField rate = new TextField();
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(remove);
        hbBtn.getChildren().add(create);
        hbBtn.getChildren().add(view);

        grid.add(indexLabel, 0, 0);
        grid.add(index, 1, 0);
        grid.add(periodLabel, 0, 1);
        grid.add(period, 1, 1);
        grid.add(rateLabel,0,2);
        grid.add(rate,1,2);
        grid.add(hbBtn, 1, 3);

        remove.setOnAction(event -> {
            int id = Integer.valueOf(index.getText());
            if (GICDeals.gicDeals.size() - 1 >= id && GICDeals.gicDeals.get(id) != null){
            GICDeals.removeDeal(id);
                showAlert(Alert.AlertType.CONFIRMATION, window, "Success!", "GIC deal removed");
                window.setScene(optionsScreen);
            }
        });
        create.setOnAction(event -> {
            int id = Integer.valueOf(index.getText());
            int month = Integer.valueOf(period.getText());
            int interest = Integer.valueOf(rate.getText());
            GICDeals deals= new GICDeals(month,interest,id);
            showAlert(Alert.AlertType.CONFIRMATION, window, "Success!", "New GIC deal created");
            window.setScene(optionsScreen);
        });
        view.setOnAction(event -> {
            viewGICDeals();
        });


        cancel.setOnAction(event -> window.setScene(optionsScreen));
    }
    void viewGICDeals() {
        ListView<String> listView = new ListView<>();

        Button goBack = new Button("Go Back");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(goBack);

        goBack.setOnAction(event -> ManageGICScreen());

        for (GICDeals deal : GICDeals.gicDeals){
            listView.getItems().add(deal.toString());
        }



        VBox vBox = new VBox();
        vBox.getChildren().add(listView);
        vBox.getChildren().add(hbBtn);
        window.setScene(new Scene(vBox, 400, 350));
    }

}
