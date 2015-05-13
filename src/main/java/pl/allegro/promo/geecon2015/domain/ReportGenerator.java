package pl.allegro.promo.geecon2015.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import pl.allegro.promo.geecon2015.domain.stats.FinancialStatisticsRepository;
import pl.allegro.promo.geecon2015.domain.stats.FinancialStats;
import pl.allegro.promo.geecon2015.domain.transaction.TransactionRepository;
import pl.allegro.promo.geecon2015.domain.transaction.UserTransaction;
import pl.allegro.promo.geecon2015.domain.transaction.UserTransactions;
import pl.allegro.promo.geecon2015.domain.user.UserRepository;

@Component
public class ReportGenerator {

    public static final BigDecimal SUM_WHEN_FAILED_TO_FETCH_TRANSACTIONS = null;
    public static final String NAME_WHEN_FAILED_TO_FETCH_USER = "<failed>";
    private final FinancialStatisticsRepository financialStatisticsRepository;

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Autowired
    public ReportGenerator(FinancialStatisticsRepository financialStatisticsRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.financialStatisticsRepository = financialStatisticsRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Report generate(ReportRequest request) {
        FinancialStats usersWithMinimalIncome = financialStatisticsRepository.listUsersWithMinimalIncome(request
                .getMinimalIncome(), request.getUsersToCheck());

        Report report = new Report();
        usersWithMinimalIncome.getUserIds().stream()
                .forEach(uuid -> addUserDataToReport(report, uuid));
        return report;
    }

    private Report addUserDataToReport(Report report, UUID uuid) {
        report.add(new ReportedUser(uuid, getUserName(uuid),
                getUserTransactionsAmount(uuid)));

        return report;
    }

    private BigDecimal getUserTransactionsAmount(UUID uuid) {
        UserTransactions userTransactions;
        try {
            userTransactions = transactionRepository.transactionsOf(uuid);
        } catch (HttpServerErrorException e) {
            return SUM_WHEN_FAILED_TO_FETCH_TRANSACTIONS;
        }

        return userTransactions.getTransactions().stream()
                .map(UserTransaction::getAmount)
                .reduce(BigDecimal.ZERO, (sum, amount) -> sum = sum.add(amount));
    }

    private String getUserName(UUID uuid) {
        try {
            return userRepository.detailsOf(uuid).getName();
        } catch (HttpServerErrorException e) {
            return NAME_WHEN_FAILED_TO_FETCH_USER;
        }
    }

}
