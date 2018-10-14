package test;

public class Service
{
}

//
//C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/mintCoin
//        {"timestamp":"2018-10-09T16:00:23.778+0000","status":404,"error":"Not Found","message":"No message available","path":"/mintCoin"}
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        {"timestamp":"2018-10-09T16:00:35.111+0000","status":404,"error":"Not Found","message":"No message available","path":"/save"}
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/supportMatrix
//        [
//        "Kenya"
//        ]
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        "YjV2WFRBcVpJckwvVnlMeFhabCt0di9jbXdJYjhMOURLMkZuSWVRRzAzZFVGRzk1UjNWNjB5by85SldtQSsyV3d5NHpuS09CYXZqdzdEYVRwYzZyU1E"
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        "YjV2WFRBcVpJckwvVnlMeFhabCt0di9jbXdJYjhMOURLMkZuSWVRRzAzZFVGRzk1UjNWNjB5by85SldtQSsyV3d5NHpuS09CYXZqdzdEYVRwYzZyU1E"
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"12345678900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        "[{\"field\":\"accountNumber\",\"inputValue\":\"12345678900987654321\",\"validity\":true,\"accountNumberCode\":\"1234\"},{\"field\":\"accountHolderName\",\"inputValue\":\"Arthur Buliva\",\"validity\":true},{\"field\":\"accountHolderAddress\",\"inputValue\":\"Hello world\",\"validity\":true},{\"field\":\"BIC\",\"inputValue\":\"SCBKENLXXXX\",\"warningMessage\":\"Should be derived!\",\"validity\":true},{\"coinId\":\"YjV2WFRBcVpJckwvVnlMeFhabCt0di9jbXdJYjhMOURLMkZuSWVRRzAzZFVGRzk1UjNWNjB5by85SldtQSsyV3d5NHpuS09CYXZqdzdEYVRwYzZyU1E\"}]"
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        {"timestamp":"2018-10-09T16:08:16.171+0000","status":500,"error":"Internal Server Error","message":"Could not validate input accountNumber: Enter a valid account number","path":"/save"}
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BICs\": \"SCBKENLXXXX\"}" localhost:8080/save
//        {"timestamp":"2018-10-09T16:08:50.072+0000","status":500,"error":"Internal Server Error","message":"BICs is not a valid input parameter for Kenya","path":"/save"}
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumbeer\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        {"timestamp":"2018-10-09T16:09:04.131+0000","status":500,"error":"Internal Server Error","message":"Essential field 'accountNumber' must have a value","path":"/save"}
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//        {"timestamp":"2018-10-09T16:09:27.444+0000","status":500,"error":"Internal Server Error","message":"Could not validate input accountNumber: Enter a valid account number","path":"/save"}
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>
//        C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Kenya\", \"accountHolderAddress\": \"Hello world\",\"accountNumbeer\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/save
//C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/validate
//        {
//        "errorMessage": "No country has been specified. You may specify any one of [Kenya, Netherlands]"
//        }
//        C:\Users\arthu>
//C:\Users\arthu>curl -X POST -H "Content-type: application/json" -d "{\"country\": \"Burundi\", \"accountHolderAddress\": \"Hello world\",\"accountNumber\": \"1234567S8900987654321\",\"accountHolderName\": \"Arthur Buliva\",\"BIC\": \"SCBKENLXXXX\"}" localhost:8080/validate
//        {
//        "country": "Burundi",
//        "errorMessage": "Burundi is unsupported. Supported countries are [Kenya, Netherlands]"
//        }
//        C:\Users\arthu>