pragma solidity ^0.4.24;
contract TransactionData{
    event NewCompany(uint companyId, string name, string address1, string property);
    event NewReceipt(uint receiptId, string from, string to, uint mount, string goods, uint return_date, string status, string remark, bool exist);

    struct Company {
        string name; //公司名称
        string address1; //公司地址
        string property; //公司性质（"bank":银行， "common_company"：一般公司）
    }

    struct Receipt {
	    string from; // 欠款公司名
	    string to; // 收款公司名
	    uint mount; // 金额
        string goods; //交易的货物（如果为"money"，则表示这是因为转让账单或向银行融资等形成的交易）
        uint return_date; // 约定还款日期
	    string status; // 状态（"bank_in"：银行参与确认了这笔交易，"firm"：律所参与确认交易，"notary_office":公证处参与确认)
        string remark; //备注信息，比如"transfer":这笔交易账单是转让款，"financing":这笔交易账单是银行融资来的，"purchase":采购交易账单
        bool exist; //账单是否还清。若已还清,则为false,未还清时为true
    }

    mapping(address=>string) mapToName;
    Company [] public companies_;
    Receipt [] public receipts_;
    uint company_num = 0;
    uint receipt_num = 0;

    //创建公司
    function createCompany(string _name, string _address, string _property) public returns (bool){
        for(uint i = 0; i < company_num; i++) {
            if(keccak256(_name) == keccak256(companies_[i].name))
                return false;
        } //如果公司名称已经存在，则创建失败
        uint id = companies_.push(Company(_name, _address, _property)) - 1; //创建Company，并push到数组中，获得当前company数组的末尾index
        company_num++;
        NewCompany(id, _name, _address, _property);
        mapToName[msg.sender] = _name; //当前调用者的地址对应的公司名设为参数里的名称
        return true;
    }

    //创建账单
    function createReceipt(string _from, string _to, uint _mount, string _goods, uint _return_date, string _status, string _remark) private returns (uint){
        uint id = receipts_.push(Receipt(_from, _to, _mount, _goods, _return_date, _status, _remark, true)) - 1;
        receipt_num++;
        NewReceipt(id, _from, _to, _mount, _goods, _return_date, _status, _remark, true);
        return id;
    }

    //根据给定的公司名字查找公司id
    function findCompany(string c) private returns (uint){
        uint cid = company_num+1;
        for(uint i = 0; i < company_num; i++){
            if(keccak256(c) == keccak256(companies_[i].name)) {
                cid = i;
                break;
            }
        }
        return cid;
    }

    //创建账单的外函数
    function registerTransaction(string _from, string _to, uint _mount, string _goods, uint _return_date, string _status, string _remark) public returns(string){
        uint companyFromId = findCompany(_from); //查找欠款公司的index
        uint companyToId = findCompany(_to); //查找收款公司的index
        if(companyFromId == (company_num+1) || companyToId == (company_num+1)) {
            return "公司不存在";
        } //如果有一方公司不存在，则创建失败
        if((keccak256(mapToName[msg.sender]) != keccak256(_from)) && (keccak256(mapToName[msg.sender]) != keccak256(_to))){
            return "没有权限";
        } //如果功能调用者不是交易双方的任一方，则创建失败
        createReceipt(_from, _to, _mount, _goods, _return_date, _status, _remark); //创建账单
        return "创建成功";
    }

    //根据欠款方，收款方，货物信息查找账单id
    function findReceipt(string from, string to, string goods) private returns (uint){
        uint rid = receipt_num+1;
        for(uint i = 0; i < receipt_num; i++){
            if((keccak256(from) == keccak256(receipts_[i].from)) && (keccak256(to) == keccak256(receipts_[i].to)) && (keccak256(goods) == keccak256(receipts_[i].goods))) {
                rid = i;
                break;
            } //若数组中存在该receipt，则返回该index，否则返回的值为数组长度+1
        }
        return rid;
    }

    //账单转让，从_from欠to1的单子中和to1欠to2的单子中扣去_mount金额，并添加_from欠to2的账单，金额为_mount
    function transferTransaction(string from1, string to1, string goods1, string to2, string goods2, uint _mount) public returns (bool){
        if(keccak256(mapToName[msg.sender]) != keccak256(to1)) {
            return false; //查看转让账单的公司是否是中间公司
        }
        uint rid1 = findReceipt(from1, to1, goods1);
        if (rid1 == receipt_num+1) {
            return false;
        }
        uint rid2 = findReceipt(to1, to2, goods2);
        if (rid2 == receipt_num+1) {
            return false;
        } //查看两份账单是否存在


        if(receipts_[rid1].mount==0) {
            return false;
        }
        if(receipts_[rid2].mount==0) {
            return false;
        }

        if(receipts_[rid1].exist==false) {
            return false;
        }
        if(receipts_[rid2].exist==false) {
            return false;
        }//查看两份账单是否有效

        if(_mount > receipts_[rid1].mount) {
            return false;
        }
        if(_mount > receipts_[rid2].mount) {
            return false;
        }//查看转让金额是否大于两份账单金额

        if(_mount == 0) {
            return false;
        } //当转让金额为0时，没必要进行操作

        receipts_[rid1].mount -= _mount;
        receipts_[rid2].mount -= _mount; //两份账单金额减少
        createReceipt(from1, to2, _mount, "money", receipts_[rid2].return_date, "bank_in", "transfer"); //创建第一个账单的欠款方和第二个账单的收款方的新账单

        if(receipts_[rid1].mount == 0) {
            receipts_[rid1].exist = false;
        }
        if(receipts_[rid2].mount == 0) {
            receipts_[rid2].exist = false;
        }//如果两份初始账单金额减少后为0，则该账单视作无效

        return true;
    }

    //根据提供的账单信息从指定银行融资相等数额
    function fromBank(string _bank, string from, string to, string goods) public returns (bool) {
        if(keccak256(mapToName[msg.sender]) != keccak256(to))
            return false; //是否是收款方在申请融资
        uint companyBankId = findCompany(_bank);
        if(companyBankId == (company_num+1)) {
            return false;
        }
        if(keccak256(companies_[companyBankId].property) != keccak256("bank")) {
            return false;
        } //提供的银行是否是真的"bank"
        uint rid = findReceipt(from, to, goods);
        if (rid == receipt_num+1) {
            return false;
        } //提供的账单是否存在
        if(receipts_[rid].exist == false) {
            return false;
        } //提供的账单是否有效
        createReceipt(to, _bank, receipts_[rid].mount, "money", 0, "", "financing"); //在银行和收款方之间建立起新的融资账单
        return true;
    }

    //根据欠款方，收款方，货物信息查找账单详细信息
    function searchForReceipt(string _from, string _to, string _goods) public returns (string, uint, string, uint, string, string) {
        uint rid = findReceipt(_from, _to, _goods);
        if (rid == receipt_num + 1) {
            return ("账单不存在", 0, "", 0, "", "");
        } //查询失败
        else {
            if(receipts_[rid].exist == false) {
                return ("账单已还清，详情如下：", receipts_[rid].mount, receipts_[rid].goods, receipts_[rid].return_date, receipts_[rid].status, receipts_[rid].remark);
            } //无效账单
            return ("账单未还清，详情如下：", receipts_[rid].mount, receipts_[rid].goods, receipts_[rid].return_date, receipts_[rid].status, receipts_[rid].remark); //账单未还清
        }
    }

    //根据rid查找账单详细信息
    function searchForReceiptWithId(uint rid) public returns (string, uint, string, uint, string, string) {
        if (rid >= receipt_num + 1) {
            return ("账单不存在", 0, "", 0, "", "");
        } //查询失败
        else {
            if(receipts_[rid].exist == false) {
                return ("账单已还清，详情如下：", receipts_[rid].mount, receipts_[rid].goods, receipts_[rid].return_date, receipts_[rid].status, receipts_[rid].remark);
            } //无效账单
            return ("账单未还清，详情如下：", receipts_[rid].mount, receipts_[rid].goods, receipts_[rid].return_date, receipts_[rid].status, receipts_[rid].remark); //账单未还清
        }
    }

    //根据给定的公司名字查找账单详细信息
    function searchForCompany(string c) public returns (string, string, string, string) {
        uint cid = findCompany(c); //查找公司的index
        if (cid == company_num+1) {
            return ("公司不存在", "", "", "");
        } //查询失败
        else {
            return ("公司详情如下：", companies_[cid].name, companies_[cid].address1, companies_[cid].property); //账单未还清
        }
    }

    //还钱
    function payback(string from, string to, string goods) public returns (bool){
        if(keccak256(mapToName[msg.sender]) != keccak256(from)) {
            return false;
        } //当前调用者是否是欠款方

        uint rid = findReceipt(from, to, goods);
        if (rid == receipt_num+1) {
            return false;
        } //该账单是否存在
        receipts_[rid].mount = 0; //欠款金额设为0
        receipts_[rid].exist = false; //账单设为无效
        return true;
    }

    //工具函数，uint转string
    function uint2str(uint i) internal returns (string c) {
        if (i == 0) return "0";
        uint j = i;
        uint length;
        while (j != 0){
            length++;
            j /= 10;
        }
        bytes memory bstr = new bytes(length);
        uint k = length - 1;
        while (i != 0){
            bstr[k--] = byte(48 + i % 10);
            i /= 10;
        }
        c = string(bstr);
    }

    //工具函数，字符串拼接
    function strConcat(string _a, string _b) internal returns (string){
        bytes memory _ba = bytes(_a);
        bytes memory _bb = bytes(_b);
        string memory ret = new string(_ba.length + _bb.length);
        bytes memory bret = bytes(ret);
        uint k = 0;
        for (uint i = 0; i < _ba.length; i++)bret[k++] = _ba[i];
        for (i = 0; i < _bb.length; i++) bret[k++] = _bb[i];
        return string(ret);
   }

    //查看哪些自己的欠款已过了约定时间
    function getMyOwnDueList(uint time_now, string from) public returns (string) {
        if(keccak256(mapToName[msg.sender]) != keccak256(from)) {
            return "";
        } //当前调用者是否是欠款方

        string memory list = "";
        bool first = true;
        for(uint i = 0; i < receipt_num; i++) {
            if((keccak256(from) == keccak256(receipts_[i].from)) && (time_now >= receipts_[i].return_date)) {
                if(first) {
                    list = strConcat(list, uint2str(i));
                    first = false;
                }
                else {
                    list = strConcat(list, "&");
                    list = strConcat(list, uint2str(i));
                }
            } //若有满足要求的账单，就把账单id加入到字符串中
        }
        return list;
    }

    //查看哪些别人欠自己的款已过了约定时间
    function getMyDueLousList(uint time_now, string to) public returns (string) {
        if(keccak256(mapToName[msg.sender]) != keccak256(to)) {
            return "";
        } //当前调用者是否是被欠款方

        string memory list = "";
        bool first = true;
        for(uint i = 0; i < receipt_num; i++) {
            if((keccak256(to) == keccak256(receipts_[i].to)) && (time_now >= receipts_[i].return_date)) {
                if(first) {
                    list = strConcat(list, uint2str(i));
                    first = false;
                }
                else {
                    list = strConcat(list, "&");
                    list = strConcat(list, uint2str(i));
                }
            } //若有满足要求的账单，就把账单id加入到字符串中
        }
        return list;
    }
}
