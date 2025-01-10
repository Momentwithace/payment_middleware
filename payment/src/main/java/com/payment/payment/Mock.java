package com.payment.payment;

import com.payment.payment.model.Bank;
import com.payment.payment.model.Billers;
import com.payment.payment.model.OtherAccounts;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Mock {

    public List<Bank> getBanks() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Access Bank", "044"));
        banks.add(new Bank("Zenith Bank", "057"));
        banks.add(new Bank("First Bank of Nigeria", "011"));
        banks.add(new Bank("United Bank for Africa", "033"));
        banks.add(new Bank("Guaranty Trust Bank", "058"));
        banks.add(new Bank("Fidelity Bank", "070"));
        banks.add(new Bank("Ecobank Nigeria", "050"));
        banks.add(new Bank("Stanbic IBTC Bank", "221"));
        banks.add(new Bank("Sterling Bank", "232"));
        banks.add(new Bank("Union Bank of Nigeria", "032"));
        banks.add(new Bank("Wema Bank", "035"));
        banks.add(new Bank("Keystone Bank", "082"));
        banks.add(new Bank("Heritage Bank", "030"));
        banks.add(new Bank("Unity Bank", "215"));
        banks.add(new Bank("Polaris Bank", "076"));
        banks.add(new Bank("SunTrust Bank", "100"));
        banks.add(new Bank("Providus Bank", "101"));
        banks.add(new Bank("Standard Chartered Bank", "068"));
        banks.add(new Bank("Jaiz Bank", "301"));
        banks.add(new Bank("Citibank Nigeria", "023"));
        return banks;
    }

    public List<OtherAccounts> getMockOtherAccounts() {
        List<OtherAccounts> accounts = new ArrayList<>();
        accounts.add(new OtherAccounts("John Doe", "1234567890", "044")); // Access Bank
        accounts.add(new OtherAccounts("Jane Smith", "9876543210", "057")); // Zenith Bank
        accounts.add(new OtherAccounts("Michael Brown", "2345678901", "011")); // First Bank
        accounts.add(new OtherAccounts("Laura Wilson", "8765432109", "033")); // UBA
        accounts.add(new OtherAccounts("Samuel Green", "3456789012", "058")); // GTBank
        accounts.add(new OtherAccounts("Sophia Adams", "7654321098", "070")); // Fidelity Bank
        accounts.add(new OtherAccounts("Benjamin White", "4567890123", "050")); // Ecobank
        accounts.add(new OtherAccounts("Emily Taylor", "6543210987", "221")); // Stanbic IBTC
        accounts.add(new OtherAccounts("Oliver Thomas", "5678901234", "232")); // Sterling Bank
        accounts.add(new OtherAccounts("Amelia Harris", "5432109876", "032")); // Union Bank
        accounts.add(new OtherAccounts("Henry Scott", "6789012345", "035")); // Wema Bank
        accounts.add(new OtherAccounts("Isabella Moore", "4321098765", "082")); // Keystone Bank
        accounts.add(new OtherAccounts("Jack Davis", "7890123456", "030")); // Heritage Bank
        accounts.add(new OtherAccounts("Mia Walker", "3210987654", "215")); // Unity Bank
        accounts.add(new OtherAccounts("William Lewis", "8901234567", "076")); // Polaris Bank
        accounts.add(new OtherAccounts("Charlotte Young", "2109876543", "100")); // SunTrust Bank
        accounts.add(new OtherAccounts("James Hall", "9012345678", "101")); // Providus Bank
        accounts.add(new OtherAccounts("Ava Allen", "0987654321", "068")); // Standard Chartered Bank
        accounts.add(new OtherAccounts("Lucas Martinez", "0123456789", "301")); // Jaiz Bank
        accounts.add(new OtherAccounts("Harper Clark", "1098765432", "023")); // Citibank Nigeria
        return accounts;
    }

    public List<Billers> getBiller() {
        List<Billers> billers = new ArrayList<>();
        billers.add(new Billers("Electricity"));
        billers.add(new Billers("Water"));
        billers.add(new Billers("Internet"));
        billers.add(new Billers("Cable TV"));
        billers.add(new Billers("Airtime"));
        return billers;
    }
}
