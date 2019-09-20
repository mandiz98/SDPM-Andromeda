package se.ju.students.axam1798.andromeda;

import android.util.Log;

public class BluetoothProtocolParser
{

    public class Statement
    {
        public boolean isComplete = false;
        public int eventKey;
        public long timestamp;
        public String data;
    }

    // Format -> 4011;TIMESTAMP;DATA\n

    private final char m_seperator = ';';
    private final char m_endOfStatement = '\n';

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
                        Log.e(TAG, "EventKey was not in the right format. Not an integer. ".concat(currentStatementPart), e);
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
                        Log.e(TAG, "Timestamp was not in the right format. Not a long. ".concat(currentStatementPart), e);
                    }
                }
                else if(statementCounter == 3 && c == m_endOfStatement)
                {
                    // Data found
                    String withoutSeparator = currentStatementPart.substring(0, currentStatementPart.length() - 1);
                    statement.data = withoutSeparator;
                    toBeRemoved = toBeRemoved.concat(currentStatementPart);

                    m_currentState = m_currentState.substring(toBeRemoved.length(), m_currentState.length());
                    statement.isComplete = true;
                    break;
                }
            }
        }

        return statement;
    }
}
