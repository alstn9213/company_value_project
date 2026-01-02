import os
import yfinance as yf
import json
import pandas as pd
from datetime import datetime

target_tickers = ["AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "JPM", "JNJ", "V", "PG", "NVDA"]

result_data = []

for ticker_symbol in target_tickers:
    print(f"[fetch_data.py] fetching {ticker_symbol}...")
    stock = yf.Ticker(ticker_symbol)
    info = stock.info

    company_data = {
        "ticker": ticker_symbol,
        "name": info.get("longName", ticker_symbol),
        "sector": info.get("sector", "Unknown"),
        "exchange": info.get("exchange", "NASDAQ"),
        "totalShares": info.get("sharesOutstanding", 0),
        "financials": [],
        "stockHistory": []
    }

    fin = stock.financials.T
    bs = stock.balance_sheet.T
    cf = stock.cashflow.T

    dates = fin.index.intersection(bs.index).intersection(cf.index)

    for date in dates[:3]: # 최근 3년치만
        try:
            fs_data = {
                "year": date.year,
                "quarter": 4, # 연간 데이터라 4분기로 통일 가정
                # Java BigDecimal 변환을 위해 모두 문자열(String)로 저장
                "revenue": str(fin.loc[date].get("Total Revenue", 0)),
                "operatingProfit": str(fin.loc[date].get("Operating Income", 0)),
                "netIncome": str(fin.loc[date].get("Net Income", 0)),
                "totalAssets": str(bs.loc[date].get("Total Assets", 0)),
                "totalLiabilities": str(bs.loc[date].get("Total Liabilities Net Minority Interest", 0)),
                "totalEquity": str(bs.loc[date].get("Stockholders Equity", 0)),
                "operatingCashFlow": str(cf.loc[date].get("Operating Cash Flow", 0)),
                "researchAndDevelopment": str(fin.loc[date].get("Research And Development", 0)), # 없을 수 있음
                "capitalExpenditure": str(cf.loc[date].get("Capital Expenditure", 0))
            }
            company_data["financials"].append(fs_data)
        except Exception as e:
            print(f"Skipping financial data for {date}: {e}")

    # 주가 데이터 (최근 1년치 일별 데이터)
    hist = stock.history(period="1y")
    for date, row in hist.iterrows():
        stock_data = {
            "date": date.strftime("%Y-%m-%d"),
            "closePrice": str(round(row["Close"], 2))
        }
        company_data["stockHistory"].append(stock_data)

    result_data.append(company_data)


script_dir = os.path.dirname(os.path.abspath(__file__))
output_path = os.path.join(script_dir, "../BACK/companyvalue/src/main/resources/data/seed_data.json")
os.makedirs(os.path.dirname(output_path), exist_ok=True)

print(f"[fetch_data.py] Saving data to: {output_path}")

# JSON 저장
with open(output_path, "w", encoding="utf-8") as f:
    json.dump(result_data, f, indent=2, ensure_ascii=False)

print("Done! JSON generated.")