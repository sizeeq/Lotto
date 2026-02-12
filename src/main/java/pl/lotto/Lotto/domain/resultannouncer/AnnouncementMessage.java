package pl.lotto.Lotto.domain.resultannouncer;

enum AnnouncementMessage {

    ID_DOES_NOT_EXIST("Given id does not exist"),
    RESULT_BEING_CALCULATED("Results are being calculated, please come back later"),
    WIN("Congratulations, you've won!"),
    LOSE("You didn't win this time. Try again!"),
    RESULT_ALREADY_CHECKED("You have already checked your results, come back later");

    final String message;

    AnnouncementMessage(String message) {
        this.message = message;
    }
}
