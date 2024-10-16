import axios from 'axios'
import { BackendUrl, apiBase, months } from '../../utils/constants'
import { OutputDataItem, convertData } from '../TradePage/utils'
import theme from '../../theme'
import numbro from 'numbro'

const colors = [
  'rgba(247, 147, 26, 0.20)',
  'rgba(34, 34, 34, 0.20)',
  'rgba(230, 0, 122, 0.20)',
  'rgba(98, 126, 234, 0.20)',
  'rgba(38, 161, 123, 0.20)',
  'rgba(25, 25, 113, 0.20)',
  'rgba(219, 201, 132, 0.20)',
]

export const fetchWatchlistData = async (
  userId: number,
  setCoinsData: { (value: any): void; (arg0: OutputDataItem[]): void },
  setLoadDashboard: { (value: boolean): void; (arg0: boolean): void }
) => {
  await axios
    .get(BackendUrl + '/cryptos', {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    .then(async (response) => {
      await convertData(response.data, userId).then((data) => {
        setCoinsData(data)
        setLoadDashboard(false)
      })
    })
}
export const getPriceHistory = async (id: any) => {
  const pData = [
    {
      date: '2023-07-09',
      price: 28065.98,
    },
    {
      date: '2023-07-08',
      price: 28765.08,
    },
    {
      date: '2023-07-07',
      price: 27725.11,
    },
    {
      date: '2023-07-06',
      price: 24961.98,
    },
    {
      date: '2023-07-05',
      price: 25065.66,
    },
    {
      date: '2023-07-04',
      price: 22065.01,
    },
    {
      date: '2023-07-03',
      price: 27000.98,
    },
  ]

  const response =
    id == 1 || id == 4 || id == 5
      ? await axios.get(BackendUrl + '/cryptos/prices/' + id, {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('token')}`,
          },
        })
      : { data: { priceHistory: pData } }
  const priceHistory = response.data.priceHistory.map(
    (item: { date: any; price: string }) => {
      return {
        x: item.date,
        y: parseInt(item.price),
      }
    }
  )
  return priceHistory
}

export const mapWatchlistData = async (coinsData: any[]) => {
  const rows = coinsData.filter(
    (item: { favorite: boolean }) => item.favorite === true
  )

  const watchlistRows = await Promise.all(
    rows.map(
      async (row: {
        id: any
        Name: { imageSrc: any; name: any; currency: any }
        Price: any
      }) => {
        const priceHistory = await getPriceHistory(row.id)
        const relevantCoin = coinsData.find((coin) => coin.id == row.id)
        const priceVals = [relevantCoin.Change, relevantCoin.Change > 0]
        return {
          id: row.id,
          src: row.Name.imageSrc,
          title: row.Name.name,
          cost: numbro(row.Price).format({ thousandSeparated: true }),
          time: '24h',
          data: [
            {
              id: row.Name.currency,
              color: '#0324fc',
              data: priceHistory,
            },
          ],
          profitloss: priceVals[0] + '%',
        }
      }
    )
  )
  return watchlistRows
}

export const getInvestingHistory = async (id: any) => {
  try {
    const response = await axios.get(BackendUrl + '/wallets/investments/', {
      params: {
        userId: id,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    console.log(response.data)
    const investmentHistory = response.data.investmentHistory.map(
      (investment: { date: any; investedAmount: string }) => {
        console.log(investment)
        return {
          x: investment.date,
          y: parseInt(investment.investedAmount),
        }
      }
    )
    console.log(
      investmentHistory.sort(
        (a, b) => Number(new Date(b.x)) - Number(new Date(a.x))
      )
    )
    return investmentHistory
  } catch (e) {
    console.log(e)
    return []
  }
}

export const mapSelectData = async (coinsData: any[]) => {
  const selectData = await Promise.all(
    coinsData.map(async (coin: { id: any; Name: { name: any } }) => {
      return {
        id: coin.id,
        label: coin.Name.name,
        color: colors[coin.id % 6],
      }
    })
  )

  return selectData
}

const calculatePercentage = (prevVal: number, newVal: number) => {
  if (prevVal === 0) return 0
  const percentage = Math.abs(prevVal - newVal) / prevVal
  const flag = prevVal <= newVal
  return [(percentage * 100).toFixed(2), flag]
}

export const mapPortfolioData = async (coinsData: any[], userId: number) => {
  const investmentHistory = await getInvestingHistory(userId)
  console.log('investmentHistory', investmentHistory)

  const portfolioData = await Promise.all(
    coinsData.map(
      async (coin: {
        id: any
        Name: { name: any }
        Price: any
        Change: any
      }) => {
        console.log('coin', coin)
        const priceHistory = await getPriceHistory(coin.id)
        const investmentVals = investmentHistory.length
          ? calculatePercentage(
              investmentHistory[1] ? investmentHistory[1].y : 0,
              investmentHistory[1] ? investmentHistory[0].y : 0
            )
          : 0

        return {
          id: coin.id,
          coinType: coin.Name.name,
          coinPercentageChange: coin.Change,
          coinValue: coin.Price,
          graphData: [
            {
              id: coin.Name.name,
              color: colors[coin.id % 6],
              data: priceHistory,
            },
            {
              id: 'Total Investment',
              color: theme.palette.primary[500],
              data: investmentHistory,
            },
          ],
          investmentPercentageChange: investmentVals[0] ?? 0,
          isCoinUp: coin.Change > 0,
          isInvestmentUp: investmentVals[1] ?? true,
          totalInvestmentValue:
            '$' + (investmentHistory.length ? investmentHistory[0].y : 0),
        }
      }
    )
  )

  return portfolioData
}

export const setCurrencyType = async (coinId: any, coinData: any[]) => {
  const coin = await coinData.find((coin: { id: any }) => coinId === coin.id)
  if (coin) return [coin.Name.name, coin.Name.currency]
  return ['', '']
}
const setCurrencyValue = async (
  coinId: any,
  coinData: any[],
  balance: number
) => {
  console.log('bal', balance)
  const coin = await coinData.find((coin: { id: any }) => coinId === coin.id)
  return balance * parseFloat(coin.Price.replace(/[$,]/g, ''))
}

export const fetchPortfolioData = async (
  userId: number,
  coinData: Array<object>
) => {
  try {
    const response = await axios.get(BackendUrl + '/wallets/portfolio', {
      params: {
        userId: userId,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    const portfolioData = await Promise.all(
      response.data.map(
        async (item: { coinId: any; balance: any; iconUrl: any }) => {
          console.log('item', item)
          const currencyType = await setCurrencyType(item.coinId, coinData)
          const currencyValue = await setCurrencyValue(
            item.coinId,
            coinData,
            item.balance
          )
          const relevantCoin = coinData.find((coin) => coin.id === item.coinId)
          console.log({
            id: item.coinId,
            iconSrc: item.iconUrl,
            currencyType: currencyType[0],
            currencyValue: currencyValue,
            currencyAcronym: currencyType[1],

            isProfit: true,
          })
          return {
            id: item.coinId,
            iconSrc: item.iconUrl,
            currencyType: currencyType[0],
            currencyValue: currencyValue,
            currencyAcronym: currencyType[1],
            profitLoss:
              relevantCoin.Change > 0
                ? '' + relevantCoin.Change + '%'
                : relevantCoin.Change + '%',
            isProfit: relevantCoin.Change > 0,
          }
        }
      )
    )
    return portfolioData
  } catch (e) {
    return []
  }
}

const setStatus = (status: string) => {
  if (status === 'pending') return '../assets/icons/transaction-pending.svg'
  if (status === 'failed') return '../assets/icons/transaction-failed.svg'
  if (status === 'success') return '../assets/icons/transaction-success.svg'
}

export const fetchWalletData = async (
  userId: number,
  coinData: Array<object>
) => {
  let response = { data: [] }
  axios
    .get(BackendUrl + '/wallets/transactions', {
      params: {
        userId: userId,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    .then((res) => (response = res))
    .catch((e) => {
      console.log(e)
    })

  let wallet: any = []
  await axios
    .get(BackendUrl + '/wallets', {
      params: {
        userId: userId,
      },
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('token')}`,
      },
    })
    .then((res) => {
      wallet = res.data
    })
    .catch(() => {
      wallet = []
    })
  console.log(response.data)
  const transactions = await Promise.all(
    response.data.map(
      async (item: {
        time: string | number | Date
        coinId: any
        status: any
        id: any
        action: string
        coinValue: string | number
        amount: any
      }) => {
        const date = new Date(item.time)
        const currency: any = await setCurrencyType(item.coinId, coinData)
        const status = setStatus(item.status)

        return {
          id: item.id,
          month: months[date.getMonth()],
          day: date.getDate(),
          currencyName: currency[0],
          imgSource: status,
          from: 'JaneCooper',
          status: item.status,
          currencyValue:
            (item.action === 'sell' ? '-' : '+') + item.coinValue + currency[1],
          convertedAmount:
            '$' + numbro(item.amount).format({ thousandSeparated: true }),
        }
      }
    )
  )
  const usdWallet = wallet.find((item) => item.coinId == 11)
  if (usdWallet) {
    console.log('transactions', transactions)
    const walletData = {
      iconSrc: '../assets/icons/usddollar.svg',
      iconTitle: usdWallet.name ?? '',
      iconAcronym: usdWallet.acronym,
      totalBalance: usdWallet.amount,
      data: transactions,
    }

    return walletData
  }
  return {}
}
