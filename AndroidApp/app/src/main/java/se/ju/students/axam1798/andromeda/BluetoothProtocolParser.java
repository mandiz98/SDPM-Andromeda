package se.ju.students.axam1798.andromeda;

import android.util.Log;

public class BluetoothProtocolParser
{

    public static class Statement
    {
        public boolean isComplete = false;
        public int eventKey;
        public long timestamp;
        public String data;

        public Statement() {}

        public Statement(int eventKey, long timestamp) {
            this(eventKey, timestamp, null);
        }

        public Statement(int eventKey, long timestamp, String data) {
            this.eventKey = eventKey;
            this.timestamp = timestamp;
            this.data = data;
        }
    }

    // Format -> 4011;TIMESTAMP;DATA\n

    private static final char m_seperator = ';';
    private static final char m_endOfStatement = '\n';

    private String m_currentState = new String();

    private String TAG = "BluetoothProtocolParser";

    BluetoothProtocolParser()
    {
    }

    Statement parse(String statementFragment)
    {
        m_currentState = m_currentState.concat(statementFragment);

        Statement statement = new Statement();

        int statementCounter = 0;
        String currentStatementPart = new String("");
        String toBeRemoved = new String();
        for(char c : m_currentState.toCharArray())
        {
            currentStatementPart += c;

            if(c == m_seperator || c == m_endOfStatement)
            {
                statementCounter++;
                if(statementCounter == 1)
                {
                    // EventKey found
                    try
                    {
                        String withoutSeparator = currentStatementPart.substring(0, currentStatementPart.length() - 1);
                        statement.eventKey = Integer.parseInt(withoutSeparator);
                        toBeRemoved = toBeRemoved.concat(currentStatementPart);
                        currentStatementPart = "";
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "EventKey was not in the right format. Not an integer. STATEMENT: ".concat(currentStatementPart), e);
                    }
                }
                else if(statementCounter == 2)
                {
                    // Timestamp found
                    try
                    {
                        String withoutSeparator = currentStatementPart.substring(0, currentStatementPart.length() - 1);
                        statement.timestamp = Long.parseLong(withoutSeparator);
                        toBeRemoved = toBeRemoved.concat(currentStatementPart);
                        currentStatementPart = "";
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, "Timestamp was not in the right format. Not a long. STATEMENT: ".concat(currentStatementPart), e);
                    }
                }
                else if(statementCounter == 3 && c == m_endOfStatement)
                {
                    // Data found
                    String withoutSeparator = currentStatementPart.substring(0, currentStatementPart.length() - 1);
                    statement.data = parseData(
                            statement.eventKey,
                            withoutSeparator
                    );
                    toBeRemoved = toBeRemoved.concat(currentStatementPart);

                    m_currentState = m_currentState.substring(toBeRemoved.length(), m_currentState.length());
                    statement.isComplete = true;
                    break;
                }
            }
        }

        return statement;
    }

    static String parse(Statement statement)
    {
        String newStatement =
                "" +
                Integer.toString(statement.eventKey) +
                m_seperator +
                Long.toString(statement.timestamp) +
                m_seperator +
                statement.data +
                m_endOfStatement;

        return newStatement;
    }

    private String parseData(int eventKey, String dataInput)
    {
        if(eventKey == 4010)
        {
            if(dataInput.charAt(0) == ' ')
            {
                return dataInput.substring(1);
            }
        }

        return dataInput;
    }
}
