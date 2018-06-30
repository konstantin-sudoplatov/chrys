package chris;

import attention.AttnDispatcherLoop;
import auxiliary.NamedConcept;
import console.ConsoleLoop;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import starter.Starter;

/**
 *  Global variables, application initialization/clearing.
 * @author su
 */
final public class Glob {

    //---***---***---***---***---***--- public data ---***---***---***---***---***--

    /** The end of the static cids range. The range goes from 0 to this. */
    public final static long MAX_STATIC_CID = 1000000;
    
    /** Application message loop. It does not need a separate thread since it works in the application thread. */
    public final static ApplicationLoop app_loop = new ApplicationLoop();
    
    /** Bidirectional converting name/cid for named concepts. */
    public final static NamedConcept named = new NamedConcept();

    /** Attention dispatcher loop. */
    public final static AttnDispatcherLoop attn_disp_loop = new AttnDispatcherLoop();

    /** Console user interface. */
    public final static ConsoleLoop console_loop = new ConsoleLoop();
    
    /**
     * Disabled constructor. This class should not ever be instantiated.
     */
    private Glob() {}

    //^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v
    //
    //                            Public methods.
    //
    //v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^

    /**
     * Initialization.
     */
    public static void initialize_application() {
        console_loop.start_thread();
        attn_disp_loop.start_thread();
        Starter.read_write_console();
        Starter.symbols();
    }
        
    /**
     * Release resources and stop all threads.
     */
    public static void terminate_application() {
        terminateMessageLoopThread(console_loop.get_thread(), console_loop);
        terminateMessageLoopThread(attn_disp_loop.get_thread(), attn_disp_loop);   // attention dispatcher stops the attention bubbles loops.
        app_loop.request_termination();
    }
        
    /**
     * Release resources and stop a message loop thread. All except master thread, because it is run by the application itself
     * (see the main() program).
     * @param thread a thread to be stopped
     * @param loop the message loop of the thread
     */
    private static void terminateMessageLoopThread(Thread thread, BaseMessageLoop loop) {

        if 
                (thread.isAlive())
        {
            try {
                loop.request_termination();
                thread.join();
            } catch (InterruptedException ex) {}
        }
    }
    
    //*****************************************************************************
    //
    //                      Common functions and tools
    //
    //*****************************************************************************
    
    /**
     * Add a concept identifier to an array.
     * @param array to be appended. null for an array of 0 elements.
     * @param cid.
     * @return appended array.
     */
    public static long[] append_array(long[] array, long cid) {
        long[] newArray;
        if
                (array == null)
            newArray = new long[1];
        else
            newArray = Arrays.copyOf(array, array.length+1);
        
        newArray[newArray.length-1] = cid;
        
        return newArray;
    }
    
    /**
     * Add an object an array of objects.
     * @param array to be appended. null for an array of 0 elements.
     * @param obj.
     * @return appended array.
     */
    public static Object[] append_array(Object[] array, Object obj) {
        Object[] newArray;
        if
                (array == null)
            newArray = new Object[1];
        else
            newArray = Arrays.copyOf(array, array.length+1);
        
        newArray[newArray.length-1] = obj;
        
        return newArray;
    }


    /**
     * Stops executing current thread for a given time period.
     * @param time milliseconds.
     */
    public static void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {}
    }

    /**
     * Check equality of two doubles with precision of 6 decimal digits.
     * @param x1
     * @param x2
     * @return
     */
    public static boolean equal(double x1, double x2) {
        if (x1 == x2)
            return true;
        else 
            return Math.abs((x1 - x2)/x2) < 1e-6;
    }

    /**
     * Check equality of two doubles with given precision.
     * @param x1
     * @param x2
     * @param numberOfDecDigits number of decimal digit to judge equality with.
     * @return
     */
    public static boolean equal(double x1, double x2, int numberOfDecDigits) {
        if (x1 == x2)
            return true;
        else {
            double точНость = Math.pow(10, numberOfDecDigits);
            return Math.abs((x1 - x2)/x2) < точНость;
        }
    }

    /**
     * Преобразовать временной промежуток в строку в человеческом виде. Если промежуток больше суток,
 сначала показывается число дней, потом time.
     * @param проМежуток
     * @return 
     */
    public static String промежуток_времени_в_String(final Date проМежуток) {
        // Вычесть дни
        int дни = (int)(проМежуток.getTime()/(1440*60000));

        SimpleDateFormat форМат = new SimpleDateFormat("HH:mm:ss.SSS");
        форМат.setTimeZone(TimeZone.getTimeZone("GMT0"));       // убрать смещение 3 часа

        if (дни > 0)
            return Integer.toString(дни) + " " + форМат.format(проМежуток);
        else
            return форМат.format(проМежуток);
    }
    public static String промежуток_времени_в_String(long вреМя) {
        return промежуток_времени_в_String(new Date(вреМя));
    }
    public static String промежуток_времени_в_String(double вреМяD) {
        return промежуток_времени_в_String(new Date((long)вреМяD));
    }

    /**
     * Преобразовать time в строку в человеческом виде. Показать time без дат.
     * @param вреМя
     * @return 
     */
    public static String время_в_String(final Date вреМя) {
        SimpleDateFormat форМат = new SimpleDateFormat("HH:mm:ss.SSS");
//        форМат.setTimeZone(TimeZone.getTimeZone("GMT0"));       // убрать смещение 3 часа
        return форМат.format(вреМя);
    }
    public static String время_в_String(long вреМя) {
        return время_в_String(new Date(вреМя));
    }
    public static String время_в_String(double вреМяD) {
        return время_в_String(new Date((long)вреМяD));
    }

    /**
     * Преобразовать дату в строку в человеческом виде.
     * @param вреМя
     * @return 
     */
    public static String дата_время_в_String(Date вреМя) {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(вреМя);
    }
    public static String date_time_to_string(long вреМя) {
        return дата_время_в_String(new Date(вреМя));
    }
    public static String дата_время_в_String(double вреМяD) {
        return дата_время_в_String(new Date((long)вреМяD));
    }

    /**
     * Преобразовать дату в строку в человеческом виде.
     * @param вреМя
     * @param форМат например, "dd.MM.yy HH:mm:ss.SSS"
     * @return 
     */
    public static String дата_время_в_String(Date вреМя, String форМат) {
        SimpleDateFormat формат = new SimpleDateFormat(форМат);
        return формат.format(вреМя);
    }
    public static String дата_время_в_String(long вреМя, String форМат) {
        return дата_время_в_String(new Date(вреМя), форМат);
    }
    public static String дата_время_в_String(double вреМя, String форМат) {
        return дата_время_в_String(new Date((long)вреМя), форМат);
    }

    /**
     * Перевести дату-время_в_String из строку в формате "dd.MM.yy hh:mm:ss.SSS" в дату.
     * @param вреМя
     * @return дата_время_в_long в милисекундах от 1970г.
     */
    public static long дата_время_в_long(String вреМя) {
        try {
            return new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").parse(вреМя).getTime();
        } catch (ParseException ex) {
            throw new Crash("Неверно задано время: " + вреМя);
        }
    }

    /**
     * Перевести дату-время_в_String из строки в формате "dd.MM.yyyy hh:mm:ss.SSS" в дату.
     * @param вреМя
     * @return Date
     */
    public static Date дата_время_в_Date(String вреМя) {
        try {
            return new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").parse(вреМя);
        } catch (ParseException ex) {
            throw new Crash("Неверно задано время: " + вреМя);
        }
    }
    /**
     * Перевести дату-время_в_String из строку в дату по заданному формату.
     * @param вреМя
     * @param форМат
     * @return Date
     */
    public static Date дата_время_в_Date(String вреМя, String форМат) {
        try {
            return new SimpleDateFormat(форМат).parse(вреМя);
        } catch (ParseException ex) {
            throw new Crash("Неверно задано время: " + вреМя);
        }
    }

    /**
     * Начать измерение временого интервала. Замеры могут быть вложенными.
     * @param стрКомментарий
     */
    public static void start_sample(String стрКомментарий) {
        стекЗамера.push(new Sample(new Date(), стрКомментарий));
    }
    
    /**
     * Завершить вложение измерения временого интервала и вывести результат.
     */
    public static void finish_sample() {
        if (стекЗамера.empty()) return;
        
        Sample заМер = стекЗамера.pop();
        long длВремя = new Date().getTime() - заМер.времяНачала.getTime();
        SimpleDateFormat форМат = new SimpleDateFormat("HH:mm:ss.SSS");
        форМат.setTimeZone(TimeZone.getTimeZone("GMT0"));       // убрать смещение 3 часа
        String стрВремя = форМат.format(new Date(длВремя));
        System.out.println("\n" + заМер.комментарий + ": " + стрВремя);
    }
    private static class Sample {
        
        public Date времяНачала;
        public String комментарий;

        /**
         * Конструктор.
         * @param времяНачала
         * @param комментарий
         */
        public Sample(Date времяНачала, String комментарий) {
            this.времяНачала = времяНачала;
            this.комментарий = комментарий;
        }
        
    }
    private static final Stack<Sample> стекЗамера = new Stack();

    /**
     * Вывести данные объекта на print.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     */
    public static void print(Object печатаемыйОбъект) {
        print(печатаемыйОбъект, null, 0);
    }

    /**
     * Вывести данные объекта на print.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param уточнение выводится вслед за классом объекта.
     */
    public static void print(Object печатаемыйОбъект, String уточнение) {
        print(печатаемыйОбъект, уточнение, 0);
    }

    /**
     * Вывести данные объекта на print.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param детальность уровень выдачи: 0 - самый краткий, 2 - самый полный
     */
    public static void print(Object печатаемыйОбъект, int детальность) {
        print(печатаемыйОбъект, null, детальность);
    }

    /**
     * Вывести данные объекта на print. Завершить print переводом строки.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     */
    public static void println(Object печатаемыйОбъект) {
        print(печатаемыйОбъект, null, 0);
        System.out.println();       // закончить пустой строкой
    }

    /**
     * Вывести данные объекта на print. Завершить print переводом строки.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param уточнение выводится вслед за классом объекта.
     */
    public static void println(Object печатаемыйОбъект, String уточнение) {
        print(печатаемыйОбъект, уточнение, 0);
        System.out.println();       // закончить пустой строкой
    }

    /**
     * Вывести данные объекта на print. Завершить print переводом строки.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param детальность уровень выдачи: 0 - самый краткий, 2 - самый полный
     */
    public static void println(Object печатаемыйОбъект, int детальность) {
        print(печатаемыйОбъект, null, детальность);
        System.out.println();       // закончить пустой строкой
    }

    /**
     * Вывести данные объекта на print. Завершить print переводом строки.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param уточнение выводится вслед за классом объекта.
     * @param детальность уровень выдачи: 0 - самый краткий, 2 - самый полный
     */
    public static void println(Object печатаемыйОбъект, String уточнение, int детальность) {
        print(печатаемыйОбъект, уточнение, детальность);
        System.out.println();       // закончить пустой строкой
    }

    /**
     * Вывести данные объекта на print.
     * @param печатаемыйОбъект объект, который имеет метод
     * в_список_строк(String уточНение, Integer деТальность)
     * @param уточнение выводится вслед за классом объекта.
     * @param детальность уровень выдачи: 0 - самый краткий, 2 - самый полный
     */
    public static void print(Object печатаемыйОбъект, String уточнение, int детальность) {

        // Проверить случай, когда печатаемый объект null
        if (печатаемыйОбъект==null) {
            System.out.println(null + ", " + уточнение);
            return;
        }

        // Найти объект метода в_список_строк()
        Method методВСпискоСтрок = null;
        try {
            методВСпискоСтрок = печатаемыйОбъект.getClass().getMethod(
                    "to_list_of_lines",
                    new Class[] {String.class, Integer.class}
            );
        } catch (NoSuchMethodException ex) {
            try {
                вызывающийОбъектПоУмолчанию = печатаемыйОбъект;
                методВСпискоСтрок = Glob.class.getMethod(
                        "to_default_list_of_lines",
                        new Class[] {String.class, Integer.class}
                );
            } catch (NoSuchMethodException ex1) {
                Logger.getLogger(Glob.class.getName()).log(Level.SEVERE,
                        "Класс {0} не имеет метода в_список_строк_по_умолчанию().",
                        печатаемыйОбъект.getClass().getName());
                return;
            }
        } catch (SecurityException ex) {
            Logger.getLogger(Glob.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Вызвать метод "в_список_строк()" и получить от вызывающего объекта список строк
        List<String> списокСтрок = null;
        try {
            списокСтрок = (List<String>) методВСпискоСтрок.invoke(печатаемыйОбъект, уточнение, детальность);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Glob.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Распечатать полученный список строк
        for(String s: списокСтрок)
            System.out.println(s);
    }

    /**
     * Если в печатаемом объекте не определен метод в_список_строк(), подставляется
     * этот метод. Цель - показывать Long и Double как даты.
     * @param уточнение
     * @param детальность
     * @return
     */
    public static List<String> to_default_list_of_lines(String уточнение, Integer детальность)
    {
        List<String> список = create_list_of_lines(вызывающийОбъектПоУмолчанию, уточнение);
        append_last_line(список, "toString() = " + вызывающийОбъектПоУмолчанию.toString());
        if
                (вызывающийОбъектПоУмолчанию instanceof Long)
            append_last_line(список, ", как дата: " +
                    date_time_to_string((Long)вызывающийОбъектПоУмолчанию));
        else
            if
                    (вызывающийОбъектПоУмолчанию instanceof Double)
            append_last_line(список, ", как дата: " +
                    дата_время_в_String((Double)вызывающийОбъектПоУмолчанию));

        return список;
    }
    /** Устанавливается в методе print(), когда печатаемый объект не имеет метода в_список_строк(). */
    private static Object вызывающийОбъектПоУмолчанию;

    /**
     * Сформировать начальный список строк - одна строка, содержащая заголовок.
     * Вызывается из метода "в_список_строк(String уточНение)" вершины иерархии конкретного класса.
     * @param вызывающийОбъект тип этого объекта выдается в первой строке перед уточнением.
     * @param уточНение выводится в первой строке списка вслед за типом объекта.
     * @return список строк выдачи.
     */
    public static List<String> create_list_of_lines(Object вызывающийОбъект, String уточНение) {
        List<String> список = new ArrayList();
        список.add(вызывающийОбъект.getClass().getSimpleName() +
                ((уточНение==null || уточНение.equals(""))? "": ", " + уточНение) + ": ");
        return список;
    }

            // Шаблон для вставки в вершину иерархии конкретного класса


//    /**
//     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
//     * @param note printed in the first line just after the object type.
//     * @param debugLevel 0 - the shortest, 2 - the fullest
//     * @return list of lines, describing this object.
//     */
//    public List<String> to_list_of_lines(String note, Integer debugLevel) {
//        List<String> lst = Glob.create_list_of_lines(this, note);
//        Glob.append_last_line(lst, String.format(""));
//
//        return lst;
//    }
//    public List<String> to_list_of_lines() {
//        return to_list_of_lines("", 2);
//    }

            // Шаблон для вставки в конкретный класс
//    /**
//     * Create list of lines, which shows the object's content. For debugging. Invoked from Glob.print().
//     * @param note printed in the first line just after the object type.
//     * @param debugLevel 0 - the shortest, 2 - the fullest
//     * @return list of lines, describing this object.
//     */
//    @Override
//    public List<String> to_list_of_lines(String note, Integer debugLevel) {
//        List<String> lst = super.to_list_of_lines(note, debugLevel);
//        Glob.add_line(lst, String.format(""));
//
//        return lst;
//    }

    /**
     * Прицепить текст к последней строке списка.
     * @param списокСтрок
     * @param строКа
     * @return преобразованный список
     */
    public static List<String> append_last_line(List<String> списокСтрок, String строКа) {
        списокСтрок.set(списокСтрок.size()-1, списокСтрок.get(списокСтрок.size()-1) + строКа);
        return списокСтрок;
    }

    /**
     * Добавить строку в список строк. Строка добавляется с отступом.
     * Позволяет работать с безымянным списком super.в_список_строк()
     * @param списокСтрок
     * @param строКа
     * @return преобразованный список
     */
    public static List<String> add_line(List<String> списокСтрок, String строКа) {
        списокСтрок.add("    " + строКа);
        return списокСтрок;
    }

    /**
     * Добавить массив строк в список строк. Строки массива становятся элементами списка.
     * Строки списка добавляются с отступом.
     * @param исходныйСписок
     * @param добавляемыйМассивСтрок
     * @return преобразованный список
     */
    public static List<String> add_array_of_lines(List<String> исходныйСписок,
            String[] добавляемыйМассивСтрок)
    {
        for(String s: добавляемыйМассивСтрок)
            исходныйСписок.add("    " + s);
        return исходныйСписок;
    }

    /**
     * Добавить список строк в список строк. Список добавляется с отступом.
     * Позволяет работать с безымянным списком super.в_список_строк()
     * @param исходныйСписок
     * @param добавляемыйСписок
     * @return преобразованный список
     */
    public static List<String> add_list_of_lines(List<String> исходныйСписок,
            List<String> добавляемыйСписок)
    {
        for(String s: добавляемыйСписок)
            исходныйСписок.add("    " + s);
        return исходныйСписок;
    }

    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%
    //
    //      Private    Private    Private    Private    Private    Private    Private
    //
    //###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%###%%%

    //---%%%---%%%---%%%---%%%---%%% private data %%%---%%%---%%%---%%%---%%%---%%%---%%%

    //---%%%---%%%---%%%---%%%---%%% private methods ---%%%---%%%---%%%---%%%---%%%---%%%--
}
