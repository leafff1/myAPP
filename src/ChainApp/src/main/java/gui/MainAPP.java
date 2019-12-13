package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.fisco.bcos.web3j.abi.datatypes.generated.Int256;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.channel.client.PEMManager;
import org.fisco.bcos.channel.client.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import web3j.TransactionData;

public class MainAPP extends JFrame{
    private TransactionData transactionData;
	private Web3j web3j;
	private ApplicationContext context;
	//private Service service;
	//private ChannelEthereumService chan;
	private Credentials credentials;

	static String bin = "";
    private static String contractAddr = "0xfbdf2cf6de0d68a2d004760fc8954d18571b31e0";
	static String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"_bank\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"goods\",\"type\":\"string\"}],\"name\":\"fromBank\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"from1\",\"type\":\"string\"},{\"name\":\"to1\",\"type\":\"string\"},{\"name\":\"goods1\",\"type\":\"string\"},{\"name\":\"to2\",\"type\":\"string\"},{\"name\":\"goods2\",\"type\":\"string\"},{\"name\":\"_mount\",\"type\":\"uint256\"}],\"name\":\"transferTransaction\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"receipts_\",\"outputs\":[{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"mount\",\"type\":\"uint256\"},{\"name\":\"goods\",\"type\":\"string\"},{\"name\":\"return_date\",\"type\":\"uint256\"},{\"name\":\"status\",\"type\":\"string\"},{\"name\":\"remark\",\"type\":\"string\"},{\"name\":\"exist\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"rid\",\"type\":\"uint256\"}],\"name\":\"searchForReceiptWithId\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"},{\"name\":\"goods\",\"type\":\"string\"}],\"name\":\"payback\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"string\"},{\"name\":\"_to\",\"type\":\"string\"},{\"name\":\"_goods\",\"type\":\"string\"}],\"name\":\"searchForReceipt\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"companies_\",\"outputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"address1\",\"type\":\"string\"},{\"name\":\"property\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_name\",\"type\":\"string\"},{\"name\":\"_address\",\"type\":\"string\"},{\"name\":\"_property\",\"type\":\"string\"}],\"name\":\"createCompany\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"c\",\"type\":\"string\"}],\"name\":\"searchForCompany\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"time_now\",\"type\":\"uint256\"},{\"name\":\"to\",\"type\":\"string\"}],\"name\":\"getMyDueLousList\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"string\"},{\"name\":\"_to\",\"type\":\"string\"},{\"name\":\"_mount\",\"type\":\"uint256\"},{\"name\":\"_goods\",\"type\":\"string\"},{\"name\":\"_return_date\",\"type\":\"uint256\"},{\"name\":\"_status\",\"type\":\"string\"},{\"name\":\"_remark\",\"type\":\"string\"}],\"name\":\"registerTransaction\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"time_now\",\"type\":\"uint256\"},{\"name\":\"from\",\"type\":\"string\"}],\"name\":\"getMyOwnDueList\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"companyId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"name\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"address1\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"property\",\"type\":\"string\"}],\"name\":\"NewCompany\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"receiptId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"from\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"to\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"mount\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"goods\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"return_date\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"status\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"remark\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"exist\",\"type\":\"bool\"}],\"name\":\"NewReceipt\",\"type\":\"event\"}]";
    //static String abi = "[{"constant":false,"inputs":[{"name":"_bank","type":"string"},{"name":"from","type":"string"},{"name":"to","type":"string"},{"name":"goods","type":"string"}],"name":"fromBank","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"from1","type":"string"},{"name":"to1","type":"string"},{"name":"goods1","type":"string"},{"name":"to2","type":"string"},{"name":"goods2","type":"string"},{"name":"_mount","type":"uint256"}],"name":"transferTransaction","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"","type":"uint256"}],"name":"receipts_","outputs":[{"name":"from","type":"string"},{"name":"to","type":"string"},{"name":"mount","type":"uint256"},{"name":"goods","type":"string"},{"name":"return_date","type":"uint256"},{"name":"status","type":"string"},{"name":"remark","type":"string"},{"name":"exist","type":"bool"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"rid","type":"uint256"}],"name":"searchForReceiptWithId","outputs":[{"name":"","type":"string"},{"name":"","type":"uint256"},{"name":"","type":"string"},{"name":"","type":"uint256"},{"name":"","type":"string"},{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"from","type":"string"},{"name":"to","type":"string"},{"name":"goods","type":"string"}],"name":"payback","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"_from","type":"string"},{"name":"_to","type":"string"},{"name":"_goods","type":"string"}],"name":"searchForReceipt","outputs":[{"name":"","type":"string"},{"name":"","type":"uint256"},{"name":"","type":"string"},{"name":"","type":"uint256"},{"name":"","type":"string"},{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"","type":"uint256"}],"name":"companies_","outputs":[{"name":"name","type":"string"},{"name":"address1","type":"string"},{"name":"property","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"_name","type":"string"},{"name":"_address","type":"string"},{"name":"_property","type":"string"}],"name":"createCompany","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"c","type":"string"}],"name":"searchForCompany","outputs":[{"name":"","type":"string"},{"name":"","type":"string"},{"name":"","type":"string"},{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"time_now","type":"uint256"},{"name":"to","type":"string"}],"name":"getMyDueLousList","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"_from","type":"string"},{"name":"_to","type":"string"},{"name":"_mount","type":"uint256"},{"name":"_goods","type":"string"},{"name":"_return_date","type":"uint256"},{"name":"_status","type":"string"},{"name":"_remark","type":"string"}],"name":"registerTransaction","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"time_now","type":"uint256"},{"name":"from","type":"string"}],"name":"getMyOwnDueList","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"anonymous":false,"inputs":[{"indexed":false,"name":"companyId","type":"uint256"},{"indexed":false,"name":"name","type":"string"},{"indexed":false,"name":"address1","type":"string"},{"indexed":false,"name":"property","type":"string"}],"name":"NewCompany","type":"event"},{"anonymous":false,"inputs":[{"indexed":false,"name":"receiptId","type":"uint256"},{"indexed":false,"name":"from","type":"string"},{"indexed":false,"name":"to","type":"string"},{"indexed":false,"name":"mount","type":"uint256"},{"indexed":false,"name":"goods","type":"string"},{"indexed":false,"name":"return_date","type":"uint256"},{"indexed":false,"name":"status","type":"string"},{"indexed":false,"name":"remark","type":"string"},{"indexed":false,"name":"exist","type":"bool"}],"name":"NewReceipt","type":"event"}]";
    private TransactionDecoder transactionDecoder = new TransactionDecoder(abi, bin);

	private static JTabbedPane mainPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel createPanel = new JPanel(new BorderLayout());
	private JPanel searchRPanel = new JPanel(new BorderLayout());
	private JPanel searchCPanel = new JPanel(new BorderLayout());
	private JPanel rePanel = new JPanel(new BorderLayout());
	private JPanel trPanel = new JPanel(new BorderLayout());
	private JPanel fromPanel = new JPanel(new BorderLayout());
	private JPanel payPanel = new JPanel(new BorderLayout());
	private JPanel restorePanel = new JPanel(new BorderLayout());
	private JPanel ownPanel = new JPanel(new BorderLayout());
	private JPanel otherPanel = new JPanel(new BorderLayout());

    public MainAPP(JFrame first, String privatekey) throws Exception{
        BigInteger gasPrice = new BigInteger("99999999999");
	    BigInteger gasLimited = new BigInteger("9999999999999");

        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		Service service = context.getBean(Service.class);
		service.run();
		ChannelEthereumService channelEthereumService = new ChannelEthereumService();
	    channelEthereumService.setChannelService(service);
	    channelEthereumService.setTimeout(10000);
	    web3j = Web3j.build(channelEthereumService, service.getGroupId());

        PEMManager pem = context.getBean(privatekey, PEMManager.class);
		ECKeyPair ecKeyPair = pem.getECKeyPair();
		credentials = GenCredential.create(ecKeyPair.getPrivateKey().toString(16));

        transactionData = TransactionData.load(contractAddr, web3j, credentials, new StaticGasProvider(gasPrice, gasLimited));
        init();
		first.setVisible(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void init() {
		this.setSize(1000, 500);
		this.setTitle("账单管理应用");

		JPanel companyContent = new JPanel();
		Box inBox1 =  Box.createVerticalBox();
		JLabel cnameLabel = new JLabel("公司名称：");
		JTextField cnameText = new JTextField();
		JButton companyButton = new JButton("查找");
		JTextField cText = new JTextField(20);
		cText.disable();
		inBox1.add(cnameLabel);
		inBox1.add(cnameText);
		inBox1.add(cText);
		inBox1.add(companyButton);
	
		companyContent.add(inBox1);


		JPanel outPanel1 = new JPanel();
		JLabel cnLabel = new JLabel("公司名称:");
		JTextField cnText = new JTextField(6);
		cnText.disable();
		JLabel caddrLabel = new JLabel("公司地址:");
		JTextField caddrText = new JTextField(2);
		caddrText.disable();
		JLabel ctypeLabel = new JLabel("公司性质:");
		JTextField ctypeText = new JTextField(10);
		ctypeText.disable();
		outPanel1.add(cnLabel);
		outPanel1.add(cnText);
		outPanel1.add(caddrLabel);
		outPanel1.add(caddrText);
		outPanel1.add(ctypeLabel);
		outPanel1.add(ctypeText);

		searchCPanel.add(companyContent);
		searchCPanel.add(outPanel1, BorderLayout.SOUTH);
        
		companyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr1;
				try {
					tr1 = transactionData.searchForCompany(cnameText.getText()).send();
					String strr =  transactionDecoder.decodeOutputReturnJson(tr1.getInput(), tr1.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					cText.setText(resul.get(0).getString("data"));
					cnText.setText(resul.get(1).getString("data"));
					caddrText.setText(resul.get(2).getString("data"));
					ctypeText.setText(resul.get(3).getString("data"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel addbContent = new JPanel();
		Box inBox2 =  Box.createVerticalBox();
		JLabel fromnameLabel = new JLabel("欠款公司名字:");
		JTextField fromnameText = new JTextField();
		JLabel tonameLabel = new JLabel("收款公司名字:");
		JTextField tonameText = new JTextField();
		JLabel mountLabel = new JLabel("交易金额:");
		JTextField mountText = new JTextField();
		JLabel goodsLabel = new JLabel("交易货物:");
		JTextField goodsText = new JTextField();
		JLabel returnLabel = new JLabel("还款期限:");
		JTextField returnText = new JTextField();
		JLabel statusLabel = new JLabel("交易状态:");
		JTextField statusText = new JTextField();
		JLabel remarkLabel = new JLabel("备注:");
		JTextField remarkText = new JTextField();
		JButton addreButton = new JButton("创建");
		inBox2.add(fromnameLabel);
		inBox2.add(fromnameText);
		inBox2.add(tonameLabel);
		inBox2.add(tonameText);
		inBox2.add(mountLabel);
		inBox2.add(mountText);
		inBox2.add(goodsLabel);
		inBox2.add(goodsText);
		inBox2.add(returnLabel);
		inBox2.add(returnText);
		inBox2.add(statusLabel);
		inBox2.add(statusText);
		inBox2.add(remarkLabel);
		inBox2.add(remarkText);
		inBox2.add(addreButton);
		addbContent.add(inBox2);

		JPanel outPanel2 = new JPanel();
		JLabel reLabel2 = new JLabel("结果:");
		JTextField reText2 = new JTextField(20);
		reText2.disable();

		outPanel2.add(reLabel2);
		outPanel2.add(reText2);
		
		restorePanel.add(addbContent);
		restorePanel.add(outPanel2, BorderLayout.SOUTH);
		
		addreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr2;
				try {
					tr2 = transactionData.registerTransaction(fromnameText.getText(), tonameText.getText(), new BigInteger(mountText.getText()), goodsText.getText(), new BigInteger(returnText.getText()), statusText.getText(), remarkText.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr2.getInput(), tr2.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);					
					reText2.setText(resul.get(0).getString("data"));
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
    
		JPanel transferContent = new JPanel();
		Box inBox3 =  Box.createVerticalBox();
		JLabel fromLabel1 = new JLabel("欠款公司1:");
		JTextField fromText1 = new JTextField();
		JLabel toLabel1 = new JLabel("收款公司1:");
		JTextField toText1 = new JTextField();
		JLabel goodsLabel1 = new JLabel("交易货物1:");
		JTextField goodsText1 = new JTextField();
		JLabel toLabel2 = new JLabel("收款公司2:");
		JTextField toText2 = new JTextField();
		JLabel goodsLabel2 = new JLabel("交易货物2:");
		JTextField goodsText2 = new JTextField();
		JLabel mountLabel3 = new JLabel("转让金额:");
		JTextField mountText3 = new JTextField();
		JButton transferButton = new JButton("转让");
		inBox3.add(fromLabel1);
		inBox3.add(fromText1);
		inBox3.add(toLabel1);
		inBox3.add(toText1);
		inBox3.add(goodsLabel1);
		inBox3.add(goodsText1);
		inBox3.add(toLabel2);
		inBox3.add(toText2);
		inBox3.add(goodsLabel2);
		inBox3.add(goodsText2);
		inBox3.add(mountLabel3);
		inBox3.add(mountText3);
		inBox3.add(transferButton);
		transferContent.add(inBox3);

		JPanel outPanel3 = new JPanel();
		JLabel reLabel3 = new JLabel("结果:");
		JTextField reText3 = new JTextField(10);
		reText3.disable();

		outPanel3.add(reLabel3);
		outPanel3.add(reText3);

		trPanel.add(transferContent);
		trPanel.add(outPanel3, BorderLayout.SOUTH);
		
		transferButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr3;
				try {
					tr3 = transactionData.transferTransaction(fromText1.getText(), toText1.getText(), goodsText1.getText(), toText2.getText(), goodsText2.getText(), new BigInteger(mountText3.getText())).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr3.getInput(), tr3.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);					
					if(resul.get(0).getString("data") == "true") {
						reText3.setText("转让账单成功");
					}
					else{
						reText3.setText("转让账单失败");
					}
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JPanel receiptContent = new JPanel();
		Box inBox4 =  Box.createVerticalBox();
		JLabel rfromLabel = new JLabel("欠款公司名称：");
		JTextField rfromText = new JTextField(20);
		JLabel rtoLabel = new JLabel("收款公司名称：");
		JTextField rtoText = new JTextField(20);
		JLabel goodsLabel4 = new JLabel("交易货物：");
		JTextField goodsText4 = new JTextField(20);
		JButton searchrButton = new JButton("查找");
		JTextField rrText = new JTextField();
		rrText.disable();
		inBox4.add(rfromLabel);
		inBox4.add(rfromText);
		inBox4.add(rtoLabel);
		inBox4.add(rtoText);
		inBox4.add(goodsLabel4);
		inBox4.add(goodsText4);
		inBox4.add(rrText);
		inBox4.add(searchrButton);
		receiptContent.add(inBox4);

		JPanel outPanel4 = new JPanel();
		JLabel mountLabel4 = new JLabel("账单金额:");
		JTextField mountText4 = new JTextField(4);
		mountText4.disable();
		JLabel goodsLabel44 = new JLabel("交易货物:");
		JTextField goodsText44 = new JTextField(2);
		goodsText44.disable();
		JLabel returnLabel4 = new JLabel("还款期限:");
		JTextField returnText4 = new JTextField(8);
		returnText4.disable();
		JLabel statusLabel4 = new JLabel("交易状态:");
		JTextField statusText4 = new JTextField(8);
		statusText4.disable();
		JLabel remarkLabel4 = new JLabel("备注:");
		JTextField remarkText4 = new JTextField(10);
		remarkText4.disable();
		outPanel4.add(mountLabel4);
		outPanel4.add(mountText4);
		outPanel4.add(goodsLabel44);
		outPanel4.add(goodsText44);
		outPanel4.add(returnLabel4);
		outPanel4.add(returnText4);
		outPanel4.add(statusLabel4);
		outPanel4.add(statusText4);
		outPanel4.add(remarkLabel4);
		outPanel4.add(remarkText4);

		searchRPanel.add(receiptContent);
		searchRPanel.add(outPanel4, BorderLayout.SOUTH);
        
		searchrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr4;
				try {
					tr4 = transactionData.searchForReceipt(rfromText.getText(), rtoText.getText(), goodsText4.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr4.getInput(), tr4.getOutput());
					System.out.println(strr);
					List<JSONObject> resul  = change(strr);
					
					rrText.setText(resul.get(0).getString("data"));
					mountText4.setText(resul.get(1).getString("data"));
					goodsText44.setText(resul.get(2).getString("data"));
					returnText4.setText(resul.get(3).getString("data"));
					statusText4.setText(resul.get(4).getString("data"));
					remarkText4.setText(resul.get(5).getString("data"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel financingContent = new JPanel();
		Box inBox5 = Box.createVerticalBox();
		JLabel bankLabel = new JLabel("银行名称:");
		JTextField bankText = new JTextField();
		JLabel fromLabel5 = new JLabel("欠款公司:");
		JTextField fromText5 = new JTextField();
		JLabel toLabel5 = new JLabel("收款公司:");
		JTextField toText5 = new JTextField();
		JLabel goodsLabel5 = new JLabel("交易货物:");
		JTextField goodsText5 = new JTextField();
		JButton financingButton = new JButton("融资");
		inBox5.add(bankLabel);
		inBox5.add(bankText);
		inBox5.add(fromLabel5);
		inBox5.add(fromText5);
		inBox5.add(toLabel5);
		inBox5.add(toText5);
		inBox5.add(goodsLabel5);
		inBox5.add(goodsText5);
		inBox5.add(financingButton);
		financingContent.add(inBox5);

		JPanel outPanel5 = new JPanel();
		JLabel reLabel5 = new JLabel("结果:");
		JTextField reText5 = new JTextField(10);
		reText5.disable();

		outPanel5.add(reLabel5);
		outPanel5.add(reText5);

		fromPanel.add(financingContent);
		fromPanel.add(outPanel5, BorderLayout.SOUTH);
		
		financingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr5;
				try {
					tr5 = transactionData.fromBank(bankText.getText(), fromText5.getText(), toText5.getText(), goodsText5.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr5.getInput(), tr5.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);					
					if(resul.get(0).getString("data") == "true") {
						reText5.setText("融资成功");
					}
					else{
						reText5.setText("融资失败");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JPanel debtContent = new JPanel();
		Box inBox6 =  Box.createVerticalBox();
		JLabel fromLabel6 = new JLabel("欠款公司名称:");
		JTextField fromText6 = new JTextField();
		JLabel toLabel6 = new JLabel("收款公司名称:");
		JTextField toText6 = new JTextField();
		JLabel goodsLabel6 = new JLabel("交易货物:");
		JTextField goodsText6 = new JTextField();
		JButton debtButton = new JButton("还款");
		inBox6.add(fromLabel6);
		inBox6.add(fromText6);
		inBox6.add(toLabel6);
		inBox6.add(toText6);
		inBox6.add(goodsLabel6);
		inBox6.add(goodsText6);
		inBox6.add(debtButton);
		debtContent.add(inBox6);

		JPanel outPanel6 = new JPanel();
		JLabel reLabel6 = new JLabel("结果:");
		JTextField reText6 = new JTextField(10);
		reText6.disable();

		outPanel6.add(reLabel6);
		outPanel6.add(reText6);

		payPanel.add(debtContent);
		payPanel.add(outPanel6, BorderLayout.SOUTH);
		
		debtButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr6;
				try {
					tr6 = transactionData.payback(fromText6.getText(), toText6.getText(), goodsText6.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr6.getInput(), tr6.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					if(resul.get(0).getString("data") == "true") {
						reText6.setText("还款成功");
					}
					else{
						reText6.setText("还款失败");
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JPanel createContent = new JPanel();
		Box inBox7 =  Box.createVerticalBox();
		JLabel nameLabel7 = new JLabel("公司名称:");
		JTextField nameText7 = new JTextField();
		JLabel addLabel7 = new JLabel("公司地址:");
		JTextField addText7 = new JTextField();
		JLabel proLabel7 = new JLabel("公司性质:");
		JTextField proText7 = new JTextField();
		JButton createButton = new JButton("创建");
		inBox7.add(nameLabel7);
		inBox7.add(nameText7);
		inBox7.add(addLabel7);
		inBox7.add(addText7);
		inBox7.add(proLabel7);
		inBox7.add(proText7);
		inBox7.add(createButton);
		createContent.add(inBox7);

		JPanel outPanel7 = new JPanel();
		JLabel reLabel7 = new JLabel("结果:");
		JTextField reText7 = new JTextField(10);
		reText7.disable();

		outPanel7.add(reLabel7);
		outPanel7.add(reText7);

		createPanel.add(createContent);
		createPanel.add(outPanel7, BorderLayout.SOUTH);
		
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr7;
				try {
					tr7 = transactionData.createCompany(nameText7.getText(), addText7.getText(), proText7.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr7.getInput(), tr7.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					if(resul.get(0).getString("data") == "true") {
						reText7.setText("创建成功");
					}
					else{
						reText7.setText("创建失败");
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});


		JPanel searcContent = new JPanel();
		Box inBox8 =  Box.createVerticalBox();
		JLabel ridLabel = new JLabel("账单编号：");
		JTextField ridText = new JTextField();
		JButton searcButton = new JButton("查找");
		JTextField rrText8 = new JTextField();
		rrText8.disable();
		inBox8.add(ridLabel);
		inBox8.add(ridText);
		inBox8.add(rrText8);
		inBox8.add(searcButton);
		searcContent.add(inBox8);

		JPanel outPanel8 = new JPanel();
		
		JLabel mountLabel8 = new JLabel("账单金额:");
		JTextField mountText8 = new JTextField(4);
		mountText8.disable();
		JLabel goodsLabel8 = new JLabel("交易货物:");
		JTextField goodsText8 = new JTextField(2);
		goodsText8.disable();
		JLabel returnLabel8 = new JLabel("还款日期:");
		JTextField returnText8 = new JTextField(8);
		returnText8.disable();
		JLabel statusLabel8 = new JLabel("交易状态:");
		JTextField statusText8 = new JTextField(8);
		statusText8.disable();
		JLabel remarkLabel8 = new JLabel("备注:");
		JTextField remarkText8 = new JTextField(10);
		remarkText8.disable();
		outPanel8.add(mountLabel8);
		outPanel8.add(mountText8);
		outPanel8.add(goodsLabel8);
		outPanel8.add(goodsText8);
		outPanel8.add(returnLabel8);
		outPanel8.add(returnText8);
		outPanel8.add(statusLabel8);
		outPanel8.add(statusText8);
		outPanel8.add(remarkLabel8);
		outPanel8.add(remarkText8);

		rePanel.add(searcContent);
		rePanel.add(outPanel8, BorderLayout.SOUTH);
        
		searcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr8;
				try {
					tr8 = transactionData.searchForReceiptWithId(new BigInteger(ridText.getText())).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr8.getInput(), tr8.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					
					rrText8.setText(resul.get(0).getString("data"));
					mountText8.setText(resul.get(1).getString("data"));
					goodsText8.setText(resul.get(2).getString("data"));
					returnText8.setText(resul.get(3).getString("data"));
					statusText8.setText(resul.get(4).getString("data"));
					remarkText8.setText(resul.get(5).getString("data"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel dueContent = new JPanel();
		Box inBox9 =  Box.createVerticalBox();
		JLabel timeLabel9 = new JLabel("现在时间：");
		JTextField timeText9 = new JTextField();
		JLabel dueLabel9 = new JLabel("欠款公司：");
		JTextField dueText9 = new JTextField();
		JButton dueButton = new JButton("查找");
		inBox9.add(timeLabel9);
		inBox9.add(timeText9);
		inBox9.add(dueLabel9);
		inBox9.add(dueText9);
		inBox9.add(dueButton);
		dueContent.add(inBox9);

		JPanel outPanel9 = new JPanel();
		JLabel dLabel9 = new JLabel("过期账单编号:");
		JTextField dText9 = new JTextField(15);
 		dText9.disable();

		outPanel9.add(dLabel9);
		outPanel9.add(dText9);

		ownPanel.add(dueContent);
		ownPanel.add(outPanel9, BorderLayout.SOUTH);
        
		dueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TransactionReceipt tr9;
				try {
					tr9 = transactionData.getMyOwnDueList(new BigInteger(timeText9.getText()), dueText9.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr9.getInput(), tr9.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					
					dText9.setText(resul.get(0).getString("data"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel dueContent10 = new JPanel();
		Box inBox10 =  Box.createVerticalBox();
		JLabel timeLabel10 = new JLabel("现在时间：");
		JTextField timeText10 = new JTextField();
		JLabel dueLabel10 = new JLabel("收款公司：");
		JTextField dueText10 = new JTextField();
		JButton dueButton10 = new JButton("查找");
		inBox10.add(timeLabel10);
		inBox10.add(timeText10);
		inBox10.add(dueLabel10);
		inBox10.add(dueText10);
		inBox10.add(dueButton10);
		dueContent10.add(inBox10);

		JPanel outPanel10 = new JPanel();
		JLabel dLabel10 = new JLabel("过期账单编号:");
		JTextField dText10 = new JTextField(15);
 		dText10.disable();

		outPanel10.add(dLabel10);
		outPanel10.add(dText10);

		otherPanel.add(dueContent10);
		otherPanel.add(outPanel10, BorderLayout.SOUTH);
        
		dueButton10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				TransactionReceipt tr10;
				try {
					tr10 = transactionData.getMyDueLousList(new BigInteger(timeText10.getText()), dueText10.getText()).send();
					String strr = transactionDecoder.decodeOutputReturnJson(tr10.getInput(), tr10.getOutput());
					System.out.println(strr);
					List<JSONObject> resul = change(strr);
					
					dText10.setText(resul.get(0).getString("data"));
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		mainPane.add("创建公司", createPanel);
		mainPane.add("查询公司", searchCPanel);
		mainPane.add("创建账单", restorePanel);
		mainPane.add("关键字查询账单", searchRPanel);
		mainPane.add("已过期的欠款账单", ownPanel);
		mainPane.add("已过期的收款账单", otherPanel);
		mainPane.add("账单号查询账单", rePanel);
		mainPane.add("转让账单", trPanel);
		mainPane.add("申请融资", fromPanel);
		mainPane.add("还款", payPanel);
		this.add(mainPane);
	}

	private List<JSONObject> change(String strr) {
		JSONObject outJson = JSON.parseObject(strr);
		JSONArray result = outJson.getJSONArray("result");
		List<JSONObject>re = new LinkedList<JSONObject>();
		for(int i = 0; i < result.size(); i ++) {
			JSONObject jsonobject = result.getJSONObject(i);
			re.add(jsonobject);
		}
		return re;
	}
}

