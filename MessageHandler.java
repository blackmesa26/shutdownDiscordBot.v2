import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MessageHandler extends ListenerAdapter {

    private boolean[] CommandBool = {
            false, // .shutdown
            false, // .reboot
            false, // .sleep
    };

    private String[] helpCommandList = {
            "`.shutdown` -принудительное завершение работы",
            "`.reboot  ` -немедленная перезагрузка компьютера",
            "`.sleep   ` -переход компьютера в режим сна",
            "`.stop    ` -остановить ввод пароля",
            "`.help    ` -остановить ввод пароля и получить список доступных команд",
    };

    private String[] CommandList = {
            ".shutdown",
            ".reboot",
            ".sleep",
    };

    private String[] consoleCommandList = {
            "shutdown.exe -f -p",      //.shutdown
            "shutdown.exe -r -t 0",    //.reboot
            "psshutdown.exe -d -t 00", //.sleep
    };
    private String[] completeCommandList = {
            "```css `Выключаем..` ```",
            "```css `Перезагрузка..` ```",
            "```css `Режим сна..` ```",
    };

    private String password = "S12345D";

    private String helpList = ">>> Command List :\n" + "%s\n" + "%s\n" + "%s\n" + "%s\n" + "%s\n";

    private int numCommand;

    private MessageChannel ChannelName;
    private User AuthorName;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        mode(event);
    }

    private void mode(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            switch (event.getMessage().getContentDisplay()) {
                case ".shutdown":
                    event.getChannel().sendMessage("T").submit();
                    setCommand(event, 0);
                case ".reboot":
                    setCommand(event, 1);
                case ".sleep":
                    setCommand(event, 2);
                case ".help":
                    stop(event, true);
                case ".stop":
                    stop(event, false);
                default:
                    runFunction(event, numCommand);
            }
        }
    }

    private void stop(MessageReceivedEvent event, boolean help) {
        if (event.getMessage().getContentDisplay().contains(".stop")) {
            CommandBool[0] = false;
            CommandBool[1] = false;
            CommandBool[2] = false;
        }
        if (help == true) {
            CommandBool[0] = false;
            CommandBool[1] = false;
            CommandBool[2] = false;
            event.getChannel().sendMessage(String.format(helpList, helpCommandList)).submit();
        }
    }

    private void runFunction(MessageReceivedEvent event, int n) {
        if (CommandBool[n] == true && ChannelName == event.getChannel()) {
            if (!event.getAuthor().isBot() && AuthorName == event.getAuthor()) {
                if (event.getMessage().getContentDisplay().contains(password)) {
                    event.getChannel().sendMessage(completeCommandList[n]).submit();
                    try {
                        Runtime.getRuntime().exec(consoleCommandList[n]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    event.getChannel().sendMessage("Пароль не верный").submit();
                }
            }
        }
    }

    private void setCommand(MessageReceivedEvent event, int n) {
        if (event.getMessage().getContentDisplay().contains(CommandList[n])) {
            ChannelName = event.getChannel();
            AuthorName = event.getAuthor();
            numCommand = n;
            event.getChannel().sendMessage("Введите пароль :").submit();
            if (CommandBool[n] == false) CommandBool[n] = true;
        }
    }
}