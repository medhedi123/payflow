import { useEffect, useState } from "react";
import api from "../services/api";

function Transactions() {
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    api.get("/transactions/me")
      .then((res) => setTransactions(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Transactions</h1>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-100">
            <tr>
              <th className="text-left p-4">Reference</th>
              <th className="text-left p-4">Type</th>
              <th className="text-left p-4">Amount</th>
              <th className="text-left p-4">Status</th>
              <th className="text-left p-4">Date</th>
            </tr>
          </thead>

          <tbody>
            {transactions.map((tx) => (
              <tr key={tx.id} className="border-t">
                <td className="p-4">{tx.reference}</td>
                <td className="p-4">{tx.type}</td>
                <td className="p-4 font-semibold">
                  {tx.amount} {tx.currency}
                </td>
                <td className="p-4">{tx.status}</td>
                <td className="p-4">
                  {new Date(tx.createdAt).toLocaleString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Transactions;