import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
} from "recharts";
import {
  ArrowDownLeft,
  ArrowUpRight,
  CreditCard,
  ReceiptText,
  Send,
  PlusCircle,
  FileText,
  Download,
  LockKeyhole,
  Eye,
  EyeOff,
} from "lucide-react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import api from "../services/api";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

const currencyRates = {
  TND: 1,
  EUR: 0.3,
  USD: 0.32,
  GBP: 0.25,
};

function Dashboard() {
  const navigate = useNavigate();
  const [showBalance, setShowBalance] = useState(false);
  const [selectedCurrency, setSelectedCurrency] = useState("TND");
  const [summary, setSummary] = useState(null);
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    api.get("/dashboard/summary")
      .then((res) => setSummary(res.data))
      .catch((err) => console.error(err));

    api.get("/transactions/me")
      .then((res) => setTransactions(res.data.slice(0, 5)))
      .catch((err) => console.error(err));
  }, []);

  if (!summary) {
    return <p className="text-muted-foreground">Loading dashboard...</p>;
  }

  const convertedBalance = (
    summary.walletBalance * currencyRates[selectedCurrency]
  ).toFixed(2);

  const convertedDeposits = (
    summary.totalDeposits * currencyRates[selectedCurrency]
  ).toFixed(2);

  const convertedTransfers = (
    summary.totalTransfersSent * currencyRates[selectedCurrency]
  ).toFixed(2);

  const chartData = [
    { name: "Mon", value: 120 },
    { name: "Tue", value: 220 },
    { name: "Wed", value: 180 },
    { name: "Thu", value: 340 },
    { name: "Fri", value: 290 },
    { name: "Sat", value: 420 },
    { name: "Sun", value: summary.walletBalance || 50 },
  ];

  return (
    <div className="space-y-8">
      <div className="flex items-start justify-between">
        <div>
          <Badge className="mb-3 bg-blue-100 text-blue-700 hover:bg-blue-100">
            Live portfolio data
          </Badge>
          <h1 className="text-4xl font-bold tracking-tight text-slate-950">
            Financial Overview
          </h1>
          <p className="text-slate-500 mt-2">
            Track wallet balance, deposits, transfers and payment activity.
          </p>
        </div>

        <div className="rounded-2xl bg-white border px-5 py-3 shadow-sm">
          <p className="text-xs text-slate-500">Current account</p>
          <p className="font-semibold">
            {localStorage.getItem("email") || "wallet@test.com"}
          </p>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-4">
        <Card
          onClick={() => navigate("/wallet")}
          className="xl:col-span-2 cursor-pointer overflow-hidden border-0 payflow-gradient text-white shadow-xl transition duration-300 hover:scale-[1.01] hover:shadow-2xl"
        >
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span>Secure Wallet Access</span>

              <div className="flex items-center gap-3">
                 <Select value={selectedCurrency} onValueChange={setSelectedCurrency}>
                    <SelectTrigger
                      onClick={(e) => e.stopPropagation()}
                      className="w-44 rounded-2xl border-white/10 bg-white/10 text-white backdrop-blur-md"
                    >
                      <SelectValue />
                    </SelectTrigger>

                    <SelectContent className="rounded-2xl border-slate-800 bg-slate-950 text-white">
                      <SelectItem value="TND">🇹🇳 Tunisian Dinar</SelectItem>
                      <SelectItem value="EUR">🇪🇺 Euro</SelectItem>
                      <SelectItem value="USD">🇺🇸 US Dollar</SelectItem>
                      <SelectItem value="GBP">🇬🇧 Pound Sterling</SelectItem>
                    </SelectContent>
                  </Select>

                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setShowBalance(!showBalance);
                  }}
                  className="rounded-xl bg-white/10 p-2 hover:bg-white/20 transition"
                >
                  {showBalance ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>

                <LockKeyhole size={20} />
              </div>
            </CardTitle>
          </CardHeader>

          <CardContent>
            <p className="text-5xl font-bold tracking-tight">
              {showBalance ? `${convertedBalance} ${selectedCurrency}` : "••••••"}
            </p>

            <div className="mt-3 text-xs text-cyan-100/70">
              Base wallet currency: TND
            </div>

            <div className="mt-8 flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-300">
                  {showBalance ? "Balance visible" : "Balance hidden for privacy"}
                </p>
                <p className="text-xs text-slate-400 mt-1">
                  Click to open confidential wallet area
                </p>
              </div>

              <Badge className="bg-emerald-400/20 text-emerald-300 hover:bg-emerald-400/20">
                Verified
              </Badge>
            </div>
          </CardContent>
        </Card>

        <StatCard
          title="Deposits"
          value={`${convertedDeposits} ${selectedCurrency}`}
          subtitle="Total money added"
          icon={ArrowDownLeft}
        />

        <StatCard
          title="Transfers Sent"
          value={`${convertedTransfers} ${selectedCurrency}`}
          subtitle="Outgoing transfers"
          icon={ArrowUpRight}
        />
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <QuickAction title="Create Invoice" subtitle="Bill a customer" icon={FileText} />
        <QuickAction title="Send Money" subtitle="Wallet transfer" icon={Send} />
        <QuickAction title="Deposit Funds" subtitle="Top up wallet" icon={PlusCircle} />
        <QuickAction title="Export Report" subtitle="Download CSV" icon={Download} />
      </div>

      <div className="grid gap-6 xl:grid-cols-3">
        <Card className="shadow-sm xl:col-span-2">
          <CardHeader>
            <CardTitle>Wallet Activity</CardTitle>
            <p className="text-sm text-slate-500">
              Simulated weekly transaction volume.
            </p>
          </CardHeader>

          <CardContent className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="payflow" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2563eb" stopOpacity={0.35} />
                    <stop offset="95%" stopColor="#2563eb" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" axisLine={false} tickLine={false} />
                <Tooltip />
                <Area
                  type="monotone"
                  dataKey="value"
                  stroke="#2563eb"
                  fillOpacity={1}
                  fill="url(#payflow)"
                  strokeWidth={3}
                />
              </AreaChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card className="shadow-sm">
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
            <p className="text-sm text-slate-500">
              Latest wallet transactions.
            </p>
          </CardHeader>

          <CardContent className="space-y-4">
            {transactions.map((tx) => (
              <div
                key={tx.id}
                className="flex items-center justify-between rounded-2xl border bg-slate-50 p-4"
              >
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-100 text-blue-700">
                    {tx.type === "DEPOSIT" ? (
                      <CreditCard size={18} />
                    ) : (
                      <ReceiptText size={18} />
                    )}
                  </div>

                  <div>
                    <p className="text-sm font-semibold">{tx.type}</p>
                    <p className="text-xs text-slate-500">{tx.status}</p>
                  </div>
                </div>

                <p className="font-bold">
                  {tx.amount} {tx.currency}
                </p>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function StatCard({ title, value, subtitle, icon: Icon }) {
  return (
    <Card className="shadow-sm">
      <CardHeader>
        <CardTitle className="flex items-center justify-between text-base">
          {title}
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-100 text-blue-700">
            <Icon size={18} />
          </div>
        </CardTitle>
      </CardHeader>

      <CardContent>
        <p className="text-3xl font-bold">{value}</p>
        <p className="mt-2 text-sm text-slate-500">{subtitle}</p>
      </CardContent>
    </Card>
  );
}

function QuickAction({ title, subtitle, icon: Icon }) {
  return (
    <Button
      variant="outline"
      className="h-auto justify-start rounded-2xl bg-white p-5 shadow-sm hover:bg-slate-50"
    >
      <div className="flex items-center gap-4 text-left">
        <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-slate-950 text-white">
          <Icon size={18} />
        </div>

        <div>
          <p className="font-semibold text-slate-950">{title}</p>
          <p className="text-xs text-slate-500">{subtitle}</p>
        </div>
      </div>
    </Button>
  );
}

export default Dashboard;